package com.glowly.identity.models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.glowly.identity.enums.AccountStatus
import com.glowly.identity.enums.Role
import com.glowly.stores.models.Store
import jakarta.persistence.CascadeType
import jakarta.persistence.*
import org.hibernate.Hibernate
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(
    name = "users",
    indexes = [
        Index(name = "idx_user_store_id", columnList = "store_id"),
        Index(name = "idx_user_email", columnList = "email"),
        Index(name = "idx_user_username", columnList = "username")
    ]
)
class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    var store: Store? = null,

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    var role: Role? = null,

    @Column(nullable = false, unique = true, length = 11)
    var cpf: String,

    @Column(nullable = false, length = 120)
    var fullName: String,

    @Column(nullable = false, unique = true, length = 20)
    var username: String,

    @Column(nullable = false, unique = true, length = 254)
    var email: String,

    @Column(nullable = false, unique = true, length = 20)
    var phone: String,

    @JsonIgnore
    @Column(nullable = false)
    var hashedPassword: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var accountStatus: AccountStatus = AccountStatus.PENDING,

    @Column(nullable = false)
    var banned: Boolean = false,

    var bannedAt: Instant? = null,

    var banExpiresAt: Instant? = null,

    @Column(nullable = false)
    var failedLoginAttempts: Int = 0,

    @Column(nullable = false)
    var tokenVersion: Int = 0,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val createdAt: Instant? = null,

    @UpdateTimestamp
    @Column(nullable = false)
    var updatedAt: Instant? = null,

    @Version
    var version: Long? = null
) {

    @PrePersist
    @PreUpdate
    fun normalize() {
        fullName = fullName.trim().uppercase()
        username = username.trim().lowercase()
        email = email.trim().lowercase()
        phone = phone.trim()
        cpf = cpf.replace(Regex("[^0-9]"), "")
    }

    fun assignStore(store: Store, role: Role) {
        this.store = store
        this.role = role
    }

    fun promote(role: Role) {
        this.role = role
    }

    fun updateProfile(
        fullName: String?,
        username: String?,
        email: String?,
        phone: String?
    ) {
        fullName?.let { this.fullName = it }
        username?.let { this.username = it }
        email?.let { this.email = it }
        phone?.let { this.phone = it }
    }

    fun changePassword(newHashedPassword: String) {
        require(newHashedPassword.isNotBlank()) { "Senha inválida." }
        hashedPassword = newHashedPassword
        revokeSessions()
    }

    fun revokeSessions() {
        tokenVersion++
    }

    fun registerFailedLogin() {
        failedLoginAttempts++
    }

    fun resetFailedLogins() {
        failedLoginAttempts = 0
    }

    fun banUntil(expiresAt: Instant?) {
        banned = true
        bannedAt = Instant.now()
        banExpiresAt = expiresAt
    }

    fun unban() {
        banned = false
        bannedAt = null
        banExpiresAt = null
        resetFailedLogins()
    }

    fun isBanExpired(): Boolean {
        return banExpiresAt?.let { Instant.now().isAfter(it) } ?: false
    }

    fun approve() {
        accountStatus = AccountStatus.APPROVED
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as User
        return id != 0L && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun toString(): String {
        return "User(id=$id, username='$username', email='$email', role=$role)"
    }
}