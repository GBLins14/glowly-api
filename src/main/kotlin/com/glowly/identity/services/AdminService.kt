package com.glowly.identity.services

import com.glowly.identity.dto.BanDto
import com.glowly.identity.dto.SetRoleDto
import com.glowly.identity.dto.UserResponse
import com.glowly.identity.dto.toResponseDTO
import com.glowly.identity.enums.AccountStatus
import com.glowly.identity.enums.Role
import com.glowly.identity.exceptions.BadRequestException
import com.glowly.identity.exceptions.ConflictException
import com.glowly.identity.exceptions.NotFoundException
import com.glowly.identity.models.User
import com.glowly.identity.repositories.AccountRepository
import com.glowly.identity.utils.MessageConstants
import com.glowly.identity.utils.validateHierarchy
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.logging.Logger
import kotlin.enums.EnumEntries

@Service
class AdminService(
    private val accountRepository: AccountRepository
) {
    private val logger = Logger.getLogger(AdminService::class.java.name)

    @Transactional
    fun approveAccount(accountId: Long) {
        val account = accountRepository.findByIdOrNull(accountId)
            ?: throw NotFoundException(MessageConstants.Error.ACCOUNT_NOT_FOUND)

        if (account.accountStatus == AccountStatus.APPROVED) {
            throw ConflictException(MessageConstants.Error.ACCOUNT_ALREADY_APPROVED)
        }

        logger.info("Approving account: ${account.id}")

        account.accountStatus = AccountStatus.APPROVED
        accountRepository.save(account)
    }

    @Transactional(readOnly = true)
    fun getAllAccounts(): List<UserResponse> =
        accountRepository.findAll().map { it.toResponseDTO() }

    @Transactional(readOnly = true)
    fun getPendingAccounts(): List<UserResponse> {
        val accounts = accountRepository.findByAccountStatus(AccountStatus.PENDING)
            ?: emptyList()

        if (accounts.isEmpty()) throw NotFoundException(MessageConstants.Error.PENDING_ACCOUNTS_NOT_FOUND)

        return accounts.map { it.toResponseDTO() }
    }

    @Transactional(readOnly = true)
    fun getAccountByLogin(login: String): UserResponse {
        val account = accountRepository.findByUsernameOrEmail(login, login)
            ?: throw NotFoundException(MessageConstants.Error.ACCOUNT_NOT_FOUND)
        return account.toResponseDTO()
    }

    @Transactional(readOnly = true)
    fun getAllRoles(): EnumEntries<Role> = Role.entries

    @Transactional
    fun updateRole(request: SetRoleDto, adminAccount: User) {
        val targetAccount = accountRepository.findByIdOrNull(request.id)
            ?: throw NotFoundException(MessageConstants.Error.ACCOUNT_NOT_FOUND)

        validateHierarchy(adminAccount, targetAccount)

        if (targetAccount.role == request.role) {
            throw ConflictException(MessageConstants.Error.ROLE_ALREADY_ASSIGNED)
        }

        logger.info("Updating role for account: ${targetAccount.id}")

        targetAccount.role = request.role
        accountRepository.save(targetAccount)
    }

    @Transactional(readOnly = true)
    fun getBannedAccounts(): List<UserResponse> {
        val accounts = accountRepository.findByBannedIsTrue() ?: emptyList()

        if (accounts.isEmpty()) {
            throw NotFoundException(MessageConstants.Error.BANNED_ACCOUNTS_NOT_FOUND)
        }

        return accounts.map { it.toResponseDTO() }
    }

    @Transactional
    fun banAccount(request: BanDto, user: User) {
        val targetAccount = accountRepository.findByIdOrNull(request.id)
            ?: throw NotFoundException(MessageConstants.Error.ACCOUNT_NOT_FOUND)

        validateHierarchy(user, targetAccount)

        if (targetAccount.banned) {
            throw ConflictException(MessageConstants.Error.ACCOUNT_ALREADY_BANNED)
        }

        logger.info("Banning account: ${targetAccount.id}")

        val now = Instant.now()

        targetAccount.apply {
            banned = true
            bannedAt = if (request.duration == null) null else now
            banExpiresAt = if (request.duration == null) null else now.plus(request.duration, request.unit)
            tokenVersion += 1
        }

        accountRepository.save(targetAccount)
    }

    @Transactional
    fun unbanAccount(accountId: Long) {
        val account = accountRepository.findByIdOrNull(accountId)
            ?: throw NotFoundException(MessageConstants.Error.ACCOUNT_NOT_FOUND)

        if (!account.banned) {
            throw BadRequestException(MessageConstants.Error.ACCOUNT_NOT_BANNED)
        }

        logger.info("Unbanning account: ${account.id}")

        account.banned = false
        account.bannedAt = null
        account.banExpiresAt = null
        accountRepository.save(account)
    }

    @Transactional
    fun deleteAccount(accountId: Long, user: User) {
        val targetAccount = accountRepository.findByIdOrNull(accountId)
            ?: throw NotFoundException(MessageConstants.Error.ACCOUNT_NOT_FOUND)

        validateHierarchy(user, targetAccount)

        logger.info("Deleting account: ${targetAccount.id}")

        accountRepository.delete(targetAccount)
    }
}