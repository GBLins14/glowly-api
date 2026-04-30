package com.glowly.identity.repositories

import com.glowly.identity.enums.AccountStatus
import com.glowly.identity.models.User
import com.glowly.stores.models.Store
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository : JpaRepository<User, Long> {
    fun findByUsernameOrEmail(username: String, email: String): User?
    fun findByCpf(cpf: String): User?
    fun findByUsername(username: String): User?
    fun findByEmail(email: String): User?
    fun findByPhone(phone: String): User?

    fun findByIdAndStore(id: Long, store: Store): User?
    fun findByUsernameOrEmailAndStore(username: String, email: String, store: Store): User?
    fun findByAccountStatusAndStore(accountStatus: AccountStatus, store: Store): List<User>?
    fun findByBannedIsTrueAndStore(store: Store): List<User>?
    fun findByStore(store: Store): List<User>
}
