package com.authenticator.authenticator_api.models

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.hibernate.Hibernate
import java.time.Instant

@Entity
@Table(name = "stores")
data class Store(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "owner_id")
    var owner: User,

    @JsonIgnore
    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var users: MutableList<User> = mutableListOf(),

    @Column(unique = true)
    var cnpj: String? = null,

    @Column(nullable = false)
    var name: String,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    @Column(nullable = false)
    var street: String,

    @Column(nullable = false)
    var number: String,

    var complement: String? = null,

    @Column(nullable = false)
    var city: String,

    @Column(nullable = false)
    var state: String,

    @Column(nullable = false)
    var zipCode: String,

    @Column(nullable = false)
    var country: String = "BR",

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false, unique = true)
    var phone: String,

    var website: String? = null,

    @Column(nullable = false)
    var active: Boolean = true,

    @Column(nullable = false)
    var createdAt: Instant = Instant.now(),

    var updatedAt: Instant = Instant.now()
) {
    @PrePersist
    @PreUpdate
    fun formatData() {
        this.cnpj = this.cnpj?.trim()
        this.name = this.name.trim()
        this.email = this.email.lowercase().trim()
        this.phone = this.phone.trim()
        this.city = this.city.uppercase()
        this.state = this.state.uppercase()
        this.country = this.country.uppercase()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Store
        return id != 0L && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun toString(): String {
        return "Store(id=$id, name='$name', email='$email', city='$city')"
    }
}