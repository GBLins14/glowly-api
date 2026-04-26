package com.glowly.stores.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class CreateStoreDto(
    val cnpj: String? = null,

    @field:NotBlank(message = "O nome da loja é obrigatório.")
    val name: String,

    val description: String? = null,

    @field:NotBlank(message = "A rua é obrigatória.")
    val street: String,

    @field:NotBlank(message = "O número é obrigatório.")
    val number: String,

    val complement: String? = null,

    @field:NotBlank(message = "A cidade é obrigatória.")
    val city: String,

    @field:NotBlank(message = "O estado é obrigatório.")
    val state: String,

    @field:NotBlank(message = "O CEP é obrigatório.")
    val zipCode: String,

    val country: String = "BR",

    @field:NotBlank(message = "O email é obrigatório.")
    @field:Email(message = "O email informado não é válido.")
    val email: String,

    @field:NotBlank(message = "O telefone é obrigatório.")
    val phone: String,

    val website: String? = null
)