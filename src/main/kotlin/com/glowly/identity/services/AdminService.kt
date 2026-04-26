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
import com.glowly.identity.utils.validateHierarchy
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.Instant
import kotlin.enums.EnumEntries

@Service
class AdminService(
    private val accountRepository: AccountRepository
) {
    @Transactional
    fun approveAccount(accountId: Long) {
        val account = accountRepository.findByIdOrNull(accountId)
            ?: throw NotFoundException("Conta não encontrada.")

        if (account.accountStatus == AccountStatus.APPROVED) {
            throw ConflictException("Esta conta já está aprovada.")
        }

        account.accountStatus = AccountStatus.APPROVED
        accountRepository.save(account)
    }

    fun getAllAccounts(): List<UserResponse> =
        accountRepository.findAll().map { it.toResponseDTO() }

    fun getPendingAccounts(): List<UserResponse> {
        val accounts = accountRepository.findByAccountStatus(AccountStatus.PENDING)
            ?: emptyList()

        if (accounts.isEmpty()) throw NotFoundException("Nenhuma conta pendente encontrada.")

        return accounts.map { it.toResponseDTO() }
    }

    fun getAccountByLogin(login: String): UserResponse {
        val account = accountRepository.findByUsernameOrEmail(login, login)
            ?: throw NotFoundException("Conta não encontrada.")
        return account.toResponseDTO()
    }

    fun getAllRoles(): EnumEntries<Role> {
        return Role.entries
    }

    @Transactional
    fun updateRole(request: SetRoleDto, adminAccount: User) {
        val targetAccount = accountRepository.findByIdOrNull(request.id)
            ?: throw NotFoundException("Conta não encontrada.")

        validateHierarchy(adminAccount, targetAccount)

        if (targetAccount.role == request.role) {
            throw ConflictException("A conta já está com este cargo.")
        }

        targetAccount.role = request.role
        accountRepository.save(targetAccount)
    }

    fun getBannedAccounts(): List<UserResponse> {
        val accounts = accountRepository.findByBannedIsTrue() ?: emptyList()

        if (accounts.isEmpty()) {
            throw NotFoundException("Nenhuma conta banida encontrada.")
        }

        return accounts.map { it.toResponseDTO() }
    }

    @Transactional
    fun banAccount(request: BanDto, user: User) {
        val targetAccount = accountRepository.findByIdOrNull(request.id)
            ?: throw NotFoundException("Conta não encontrada.")

        validateHierarchy(user, targetAccount)

        if (targetAccount.banned) {
            throw ConflictException("Esta conta já está bloqueada.")
        }

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
            ?: throw NotFoundException("Conta não encontrada.")

        if (!account.banned) {
            throw BadRequestException("A conta não está banida.")
        }

        account.banned = false
        account.bannedAt = null
        account.banExpiresAt = null
        accountRepository.save(account)
    }

    @Transactional
    fun deleteAccount(accountId: Long, user: User) {
        val targetAccount = accountRepository.findByIdOrNull(accountId)
            ?: throw NotFoundException("Conta não encontrada.")

        validateHierarchy(user, targetAccount)

        accountRepository.delete(targetAccount)
    }
}