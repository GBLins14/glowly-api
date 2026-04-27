package com.glowly.stores.models

import com.glowly.identity.models.User
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(
    name = "stores",
    indexes = [
        Index(name = "idx_store_owner_id", columnList = "owner_id")
    ]
)
class Store(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    val owner: User,

    @Column(name = "cnpj", unique = true, length = 14)
    var cnpj: String? = null,

    @Column(nullable = false, length = 50)
    var name: String,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    @Column(nullable = false, length = 80)
    var street: String,

    @Column(nullable = false, length = 20)
    var number: String,

    @Column(length = 80)
    var complement: String? = null,

    @Column(nullable = false, length = 50)
    var city: String,

    @Column(nullable = false, length = 50)
    var state: String,

    @Column(nullable = false, length = 10)
    var zipCode: String,

    @Column(nullable = false, length = 2)
    var country: String = "BR",

    @Column(nullable = false, length = 254)
    var email: String,

    @Column(nullable = false, length = 20)
    var phone: String,

    @Column(length = 120)
    var website: String? = null,

    @Column(nullable = false)
    var active: Boolean = true,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(nullable = false)
    var updatedAt: LocalDateTime? = null,

    @Version
    var version: Long? = null
) {

    fun activate() {
        require(!active) { "Store já está ativa." }
        active = true
    }

    fun deactivate() {
        require(active) { "Store já está inativa." }
        active = false
    }

    fun updateDetails(
        name: String?,
        description: String?,
        street: String?,
        number: String?,
        complement: String?,
        city: String?,
        state: String?,
        zipCode: String?,
        country: String?,
        email: String?,
        phone: String?,
        website: String?
    ) {
        name?.let { this.name = it }
        description?.let { this.description = it }
        street?.let { this.street = it }
        number?.let { this.number = it }
        complement?.let { this.complement = it }
        city?.let { this.city = it }
        state?.let { this.state = it }
        zipCode?.let { this.zipCode = it }
        country?.let { this.country = it }
        email?.let { this.email = it }
        phone?.let { this.phone = it }
        website?.let { this.website = it }
    }
}