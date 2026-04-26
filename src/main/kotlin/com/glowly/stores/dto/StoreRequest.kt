package com.glowly.stores.dto

data class CreateStoreDto(
    val cnpj: String? = null,
    val name: String,
    val description: String? = null,
    val street: String,
    val number: String,
    val complement: String? = null,
    val city: String,
    val state: String,
    val zipCode: String,
    val country: String = "BR",
    val email: String,
    val phone: String,
    val website: String? = null
)