package com.glowly.identity.services

import com.glowly.identity.dto.SignInDto
import com.glowly.identity.dto.SignUpDto
import com.glowly.identity.dto.UserResponse
import com.glowly.identity.dto.toResponseDTO
import com.glowly.identity.enums.AccountStatus
import com.glowly.identity.enums.Role
import com.glowly.identity.exceptions.BadRequestException
import com.glowly.identity.exceptions.NotFoundException
import com.glowly.identity.exceptions.UnauthorizedException
import com.glowly.identity.models.PasswordResetToken
import com.glowly.identity.models.User
import com.glowly.identity.repositories.AccountRepository
import com.glowly.identity.repositories.TokenRepository
import com.glowly.identity.security.Hash
import com.glowly.identity.security.JwtUtil
import com.glowly.identity.utils.MessageConstants
import com.glowly.identity.utils.checkDuplicate
import com.glowly.identity.utils.generateToken
import com.glowly.identity.utils.hashToken
import com.glowly.stores.repositories.StoreRepository
import org.springframework.transaction.annotation.Transactional
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.ConcurrentHashMap

@Service
class AuthService(
    private val accountRepository: AccountRepository,
    private val tokenRepository: TokenRepository,
    private val storeRepository: StoreRepository,
    private val jwtUtil: JwtUtil,
    private val bcrypt: Hash,
    private val validatorUtil: ValidatorService,
    private val forgotPasswordService: ForgotPasswordService,
    @Value($$"${app.frontend-url}") private val FRONTEND_URL: String,
    @Value($$"${app.password-recovery.token-expiration-minutes}") private val TOKEN_EXPIRATION_MINUTES: Long,
    @Value($$"${app.sign.min-password-length}") private val MIN_PASSWORD_LENGTH: Int,
    @Value($$"${app.sign.max-password-length}") private val MAX_PASSWORD_LENGTH: Int,
    @Value($$"${app.sign.max-attempts}") private val MAX_ATTEMPTS: Int,
    @Value($$"${app.sign.lockout-minutes}") private val LOCKOUT_MINUTES: Long,
) {
    private val log: Logger = LoggerFactory.getLogger(javaClass)
    private val dummyHash = bcrypt.encodePassword("dummyTimingAttackPrevention") ?: ""
    private val passwordResetAttempts = ConcurrentHashMap<String, Instant>()

    @Transactional
    fun register(request: SignUpDto): String {
        val existingCpf = accountRepository.findByCpf(request.cpf)
        val existingUsername = accountRepository.findByUsername(request.username)
        val existingEmail = accountRepository.findByEmail(request.email)
        val existingPhone = accountRepository.findByPhone(request.phone)

        if ((request.role == null) != (request.storeId == null)) {
            throw BadRequestException(MessageConstants.Error.INVALID_STORE_ROLE)
        }

        val store = request.storeId?.let { storeId ->
            storeRepository.findById(storeId)
                .orElseThrow { NotFoundException(MessageConstants.Error.STORE_NOT_FOUND) }
        }

        checkDuplicate(existingCpf, MessageConstants.Error.DUPLICATE_CPF)
        checkDuplicate(existingUsername, MessageConstants.Error.DUPLICATE_USERNAME)
        checkDuplicate(existingEmail, MessageConstants.Error.DUPLICATE_EMAIL)
        checkDuplicate(existingPhone, MessageConstants.Error.DUPLICATE_PHONE)

        val (accountStatus, finalRole, messageReturn) = when (request.role) {
            Role.USER -> Triple(
                AccountStatus.APPROVED,
                Role.USER,
                MessageConstants.Success.ACCOUNT_REGISTERED
            )
            Role.ADMIN -> Triple(
                AccountStatus.PENDING,
                Role.ADMIN,
                MessageConstants.Success.ACCOUNT_REGISTERED_PENDING
            )
            else -> Triple(
                AccountStatus.APPROVED,
                null,
                MessageConstants.Success.ACCOUNT_REGISTERED
            )
        }

        val user = User(
            store = store,
            role = finalRole,
            cpf = request.cpf,
            fullName = request.fullName,
            username = request.username,
            email = request.email,
            phone = request.phone,
            hashedPassword = bcrypt.encodePassword(request.password) ?: throw RuntimeException("Failed to encode password"),
            accountStatus = accountStatus
        )

        accountRepository.save(user)
        return messageReturn
    }

    @Transactional
    fun login(request: SignInDto): String {
        val login = request.login.lowercase().trim()
        val user = accountRepository.findByUsernameOrEmail(login, login)

        if (user == null) {
            bcrypt.checkPassword(request.password, dummyHash)
            throw UnauthorizedException(MessageConstants.Error.INVALID_CREDENTIALS)
        }

        if (user.banned) {
            if (user.banExpiresAt == null) {
                throw UnauthorizedException(MessageConstants.Error.ACCOUNT_BANNED_PERMANENT)
            }

            if (!user.isBanExpired()) {
                throw UnauthorizedException(MessageConstants.Error.ACCOUNT_BANNED_TEMPORARY)
            }

            user.apply {
                banned = false
                bannedAt = null
                banExpiresAt = null
                failedLoginAttempts = 0
            }
            accountRepository.save(user)
        }

        val now = Instant.now()

        if (!bcrypt.checkPassword(request.password, user.hashedPassword)) {
            user.failedLoginAttempts += 1

            if (user.failedLoginAttempts >= MAX_ATTEMPTS) {
                user.banned = true
                user.bannedAt = now
                user.banExpiresAt = now.plus(LOCKOUT_MINUTES, ChronoUnit.MINUTES)
                accountRepository.save(user)

                throw UnauthorizedException(
                    MessageConstants.Error.ACCOUNT_LOCKED_ATTEMPTS.format(LOCKOUT_MINUTES)
                )
            }

            accountRepository.save(user)
            throw UnauthorizedException(MessageConstants.Error.INVALID_CREDENTIALS)
        }

        if (user.accountStatus == AccountStatus.PENDING) {
            throw UnauthorizedException(MessageConstants.Error.ACCOUNT_PENDING)
        }

        user.failedLoginAttempts = 0
        accountRepository.save(user)

        val token = jwtUtil.generateToken(user.username, user.role, user.tokenVersion)

        return token
    }

    @Transactional
    fun processForgotPassword(email: String) {
        val now = Instant.now()
        val lastAttempt = passwordResetAttempts[email]
        if (lastAttempt != null && now.isBefore(lastAttempt.plus(5, ChronoUnit.MINUTES))) {
            return
        }
        passwordResetAttempts[email] = now

        val user = accountRepository.findByEmail(email) ?: return
        tokenRepository.deleteByUser(user)
        tokenRepository.flush()

        val rawToken = generateToken()
        val tokenHash = hashToken(rawToken)

        val tokenEntity = PasswordResetToken(
            tokenHash = tokenHash,
            user = user,
            expiryDate = Instant.now().plus(TOKEN_EXPIRATION_MINUTES, ChronoUnit.MINUTES)
        )
        tokenRepository.save(tokenEntity)

        val link = "$FRONTEND_URL/reset-password?token=$rawToken"

        try {
            forgotPasswordService.send(email, user.username, link)
        } catch (e: Exception) {
            log.error("Falha ao enviar email de recuperação para $email", e)
        }
    }

    @Transactional
    fun processResetPassword(token: String, newPassword: String) {
        val tokenHash = hashToken(token)
        val resetToken = tokenRepository.findByTokenHash(tokenHash)
            ?: throw NotFoundException(MessageConstants.Error.TOKEN_NOT_FOUND)

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken)
            throw UnauthorizedException(MessageConstants.Error.TOKEN_EXPIRED)
        }

        val user = resetToken.user

        user.hashedPassword = bcrypt.encodePassword(newPassword) ?: throw RuntimeException("Failed to encode password")
        user.tokenVersion += 1

        accountRepository.save(user)
        tokenRepository.delete(resetToken)
        SecurityContextHolder.clearContext()
    }

    @Transactional(readOnly = true)
    fun getMe(user: User): UserResponse {
        return user.toResponseDTO()
    }

    @Transactional
    fun logout(user: User) {
        user.tokenVersion += 1
        accountRepository.save(user)
        SecurityContextHolder.clearContext()
    }
}