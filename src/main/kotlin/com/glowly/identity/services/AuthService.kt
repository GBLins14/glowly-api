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
import com.glowly.identity.utils.checkDuplicate
import com.glowly.identity.utils.generateToken
import com.glowly.identity.utils.hashToken
import com.glowly.stores.repositories.StoreRepository
import java.util.concurrent.ConcurrentHashMap
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

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
    @Value($$"${app.sign.min-fullname-length}") private val MIN_FULLNAME_LENGTH: Int,
    @Value($$"${app.sign.min-username-length}") private val MIN_USERNAME_LENGTH: Int,
    @Value($$"${app.sign.max-username-length}") private val MAX_USERNAME_LENGTH: Int,
    @Value($$"${app.sign.min-password-length}") private val MIN_PASSWORD_LENGTH: Int,
    @Value($$"${app.sign.max-password-length}") private val MAX_PASSWORD_LENGTH: Int,
    @Value($$"${app.sign.max-attempts}") private val MAX_ATTEMPTS: Int,
    @Value($$"${app.sign.lockout-minutes}") private val LOCKOUT_MINUTES: Long,
) {
    private val logger = LoggerFactory.getLogger(AuthService::class.java)
    private val dummyHash = bcrypt.encodePassword("dummyTimingAttackPrevention") ?: ""
    private val passwordResetAttempts = ConcurrentHashMap<String, Instant>()

    @Transactional
    fun register(request: SignUpDto): String {
        val cleanedCpf = validatorUtil.cleanCpfOrCnpj(request.cpf)
        val username = request.username.lowercase().trim()

        if (!validatorUtil.isValidCpf(cleanedCpf)) {
            throw BadRequestException("É necessário inserir um número de CPF que seja válido.")
        }

        if (request.fullName.length < MIN_FULLNAME_LENGTH) {
            throw BadRequestException("É necessário inserir o seu nome completo.")
        }

        if (username.length !in MIN_USERNAME_LENGTH..MAX_USERNAME_LENGTH) {
            throw BadRequestException("O nome de usuário deve conter no mínimo $MIN_USERNAME_LENGTH caracteres, e no máximo $MAX_USERNAME_LENGTH caracteres.")
        }

        if (!validatorUtil.isValidEmail(request.email)) {
            throw BadRequestException("É necessário inserir um endereço de email que seja válido.")
        }

        if (!validatorUtil.isValidPhone(request.phone)) {
            throw BadRequestException("É necessário inserir um número de telefone que seja válido.")
        }

        if (request.password.length !in MIN_PASSWORD_LENGTH..MAX_PASSWORD_LENGTH) {
            throw BadRequestException("A senha deve conter no mínimo $MIN_PASSWORD_LENGTH caracteres, e no máximo $MAX_PASSWORD_LENGTH caracteres.")
        }

//        if (!validatorUtil.isValidPassword(request.password)) {
//            throw BadRequestException("A senha deve conter pelo menos uma letra maiúscula, uma minúscula, um número e um caractere especial.")
//        }

        val existingCpf = accountRepository.findByCpf(cleanedCpf)
        val existingUsername = accountRepository.findByUsername(username)
        val existingEmail = accountRepository.findByEmail(request.email)
        val existingPhone = accountRepository.findByPhone(request.phone)

        if ((request.role == null) != (request.storeId == null)) {
            throw BadRequestException(
                "Os campos 'store' e 'role' devem ser informados juntos ou ambos omitidos."
            )
        }

        val store = request.storeId?.let { storeId ->
            storeRepository.findById(storeId)
                .orElseThrow {
                    NotFoundException("A loja com o ID informado não existe.")
                }
        }

        checkDuplicate(existingCpf, "Já existe uma conta registrada com este número de CPF.")
        checkDuplicate(existingUsername, "Já existe uma conta registrada com este nome de usuário.")
        checkDuplicate(existingEmail, "Já existe uma conta registrada com este endereço de email.")
        checkDuplicate(existingPhone, "Já existe uma conta registrada com este número de telefone.")

        val (accountStatus, finalRole, messageReturn) = when (request.role) {
            Role.USER -> Triple(
                AccountStatus.APPROVED,
                Role.USER,
                "Conta registrada com sucesso."
            )
            Role.ADMIN -> Triple(
                AccountStatus.PENDING,
                Role.ADMIN,
                "Conta registrada com sucesso, aguarde a liberação de outro admin."
            ) else -> {
                Triple(
                    AccountStatus.APPROVED,
                    null,
                    "Conta registrada com sucesso."
                )
            }
        }

        val user = User(
            store = store,
            role = finalRole,
            cpf = cleanedCpf,
            fullName = request.fullName,
            username = username,
            email = request.email,
            phone = request.phone,
            hashedPassword = bcrypt.encodePassword(request.password),
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
            throw UnauthorizedException("Usuário ou senha incorretos.")
        }

        if (user.banned) {
            if (user.banExpiresAt == null) {
                throw UnauthorizedException("Sua conta está permanentemente bloqueada.")
            }

            if (!user.isBanExpired()) {
                throw UnauthorizedException("Conta temporariamente bloqueada. Tente novamente mais tarde.")
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

                throw UnauthorizedException("Conta bloqueada devido a tentativas excessivas.")
            }

            accountRepository.save(user)
            throw UnauthorizedException("Usuário ou senha incorretos.")
        }

        if (user.accountStatus == AccountStatus.PENDING) {
            throw UnauthorizedException("A sua conta ainda não foi aprovada, aguarde a liberação.")
        }

        user.failedLoginAttempts = 0
        accountRepository.save(user)

        val token = jwtUtil.generateToken(user.username, user.role, user.tokenVersion)

        return token
    }

    @Transactional
    fun processForgotPassword(email: String) {
        val email = email.lowercase().trim()

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
            logger.error("Falha ao enviar email de recuperação para $email", e)
        }
    }

    @Transactional
    fun processResetPassword(token: String, newPassword: String) {
        val tokenHash = hashToken(token)
        val resetToken = tokenRepository.findByTokenHash(tokenHash)
            ?: throw NotFoundException("Token inválido ou não encontrado.")

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken)
            throw UnauthorizedException("Este link expirou. Solicite uma nova recuperação.")
        }

        if (newPassword.length !in MIN_PASSWORD_LENGTH..MAX_PASSWORD_LENGTH) {
            throw BadRequestException("A senha deve conter no mínimo $MIN_PASSWORD_LENGTH caracteres, e no máximo $MAX_PASSWORD_LENGTH caracteres.")
        }

//        if (!validatorUtil.isValidPassword(newPassword)) {
//            throw BadRequestException("A senha deve conter pelo menos uma letra maiúscula, uma minúscula, um número e um caractere especial.")
//        }

        val user = resetToken.user

        user.hashedPassword = bcrypt.encodePassword(newPassword)
        user.tokenVersion += 1

        accountRepository.save(user)
        tokenRepository.delete(resetToken)
        SecurityContextHolder.clearContext()
    }

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