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
    fun approveAccount(adminAccount: User, targetAccountId: Long) {
        val targetAccount = accountRepository.findByIdAndStore(targetAccountId, adminAccount.store!!)
            ?: throw NotFoundException(MessageConstants.Error.ACCOUNT_NOT_FOUND)

        validateHierarchy(adminAccount, targetAccount)

        if (targetAccount.accountStatus == AccountStatus.APPROVED) {
            throw ConflictException(MessageConstants.Error.ACCOUNT_ALREADY_APPROVED)
        }

        logger.info("Approving account: ${targetAccount.id}")

        targetAccount.approve()
        accountRepository.save(targetAccount)
    }

    @Transactional(readOnly = true)
    fun getAllAccounts(adminAccount: User): List<UserResponse> =
        accountRepository.findByStore(adminAccount.store!!).map { it.toResponseDTO() }

    @Transactional(readOnly = true)
    fun getPendingAccounts(adminAccount: User): List<UserResponse> {
        val accounts = accountRepository.findByAccountStatusAndStore(AccountStatus.PENDING, adminAccount.store!!)
            ?: emptyList()

        if (accounts.isEmpty()) throw NotFoundException(MessageConstants.Error.PENDING_ACCOUNTS_NOT_FOUND)

        return accounts.map { it.toResponseDTO() }
    }

    @Transactional(readOnly = true)
    fun getAccountByLogin(adminAccount: User, login: String): UserResponse {
        val account = accountRepository.findByUsernameOrEmailAndStore(login, login, adminAccount.store!!)
            ?: throw NotFoundException(MessageConstants.Error.ACCOUNT_NOT_FOUND)
        return account.toResponseDTO()
    }

    @Transactional(readOnly = true)
    fun getAllRoles(): EnumEntries<Role> = Role.entries

    @Transactional
    fun updateRole(request: SetRoleDto, adminAccount: User) {
        val targetAccount = accountRepository.findByIdAndStore(request.id, adminAccount.store!!)
            ?: throw NotFoundException(MessageConstants.Error.ACCOUNT_NOT_FOUND)

        validateHierarchy(adminAccount, targetAccount)

        if (targetAccount.role == request.role) {
            throw ConflictException(MessageConstants.Error.ROLE_ALREADY_ASSIGNED)
        }

        logger.info("Updating role for account: ${targetAccount.id}")

        targetAccount.promote(request.role)
        accountRepository.save(targetAccount)
    }

    @Transactional(readOnly = true)
    fun getBannedAccounts(adminAccount: User): List<UserResponse> {
        val accounts = accountRepository.findByBannedIsTrueAndStore(adminAccount.store!!) ?: emptyList()

        if (accounts.isEmpty()) {
            throw NotFoundException(MessageConstants.Error.BANNED_ACCOUNTS_NOT_FOUND)
        }

        return accounts.map { it.toResponseDTO() }
    }

    @Transactional
    fun banAccount(request: BanDto, adminAccount: User) {
        val targetAccount = accountRepository.findByIdAndStore(request.id, adminAccount.store!!)
            ?: throw NotFoundException(MessageConstants.Error.ACCOUNT_NOT_FOUND)

        validateHierarchy(adminAccount, targetAccount)

        if (targetAccount.banned) {
            throw ConflictException(MessageConstants.Error.ACCOUNT_ALREADY_BANNED)
        }

        logger.info("Banning account: ${targetAccount.id}")

        val now = Instant.now()

        targetAccount.banUntil(request.duration, request.unit)

        accountRepository.save(targetAccount)
    }

    @Transactional
    fun unbanAccount(adminAccount: User, targetAccountId: Long) {
        val targetAccount = accountRepository.findByIdAndStore(targetAccountId, adminAccount.store!!)
            ?: throw NotFoundException(MessageConstants.Error.ACCOUNT_NOT_FOUND)

        validateHierarchy(adminAccount, targetAccount)

        if (!targetAccount.banned) {
            throw BadRequestException(MessageConstants.Error.ACCOUNT_NOT_BANNED)
        }

        logger.info("Unbanning account: ${targetAccount.id}")

        targetAccount.unban()

        accountRepository.save(targetAccount)
    }

    @Transactional
    fun deleteAccount(targetAccountId: Long, adminAccount: User) {
        val targetAccount = accountRepository.findByIdAndStore(targetAccountId, adminAccount.store!!)
            ?: throw NotFoundException(MessageConstants.Error.ACCOUNT_NOT_FOUND)

        validateHierarchy(adminAccount, targetAccount)

        logger.info("Deleting account: ${targetAccount.id}")

        accountRepository.delete(targetAccount)
    }
}