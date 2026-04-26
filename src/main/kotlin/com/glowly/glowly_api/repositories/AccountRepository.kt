package com.glowly.glowly_api.repositories

import com.glowly.glowly_api.enums.AccountStatus
import com.glowly.glowly_api.models.User
import org.springframework.data.jpa.repository.JpaRepository

interface AccountRepository : JpaRepository<User, Long> {
    fun findByUsernameOrEmail(username: String, email: String): User?
    fun findByCpf(cpf: String): User?
    fun findByUsername(username: String): User?
    fun findByEmail(email: String): User?
    fun findByPhone(phone: String): User?
    fun findByAccountStatus(accountStatus: AccountStatus): List<User>?
    fun findByBannedIsTrue(): List<User>?
}
