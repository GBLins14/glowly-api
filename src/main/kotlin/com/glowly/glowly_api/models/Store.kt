package com.glowly.glowly_api.models

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "stores")
data class Store(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    val owner: User,

    @Column(unique = true)
    val cnpj: String? = null,

    @Column(nullable = false)
    val name: String,

    @Column(columnDefinition = "TEXT")
    val description: String? = null,

    @Column(nullable = false)
    val street: String,

    @Column(nullable = false)
    val number: String,

    val complement: String? = null,

    @Column(nullable = false)
    val city: String,

    @Column(nullable = false)
    val state: String,

    @Column(nullable = false)
    val zipCode: String,

    @Column(nullable = false)
    val country: String = "BR",

    @Column(nullable = false)
    val email: String,

    @Column(nullable = false)
    val phone: String,

    val website: String? = null,

    @Column(nullable = false)
    val active: Boolean = true,

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)