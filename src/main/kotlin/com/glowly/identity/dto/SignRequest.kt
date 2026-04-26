package com.glowly.identity.dto

import com.glowly.identity.enums.Role
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class SignUpDto(
    val storeId: Long? = null,
    val role: Role? = null,

    @field:NotBlank(message = "O CPF é obrigatório.")
    @field:Size(min = 11, max = 14, message = "O CPF deve conter entre 11 e 14 caracteres.")
    val cpf: String,

    @field:NotBlank(message = "O nome completo é obrigatório.")
    @field:Size(min = 2, message = "O nome completo deve conter pelo menos 2 caracteres.")
    val fullName: String,

    @field:NotBlank(message = "O nome de usuário é obrigatório.")
    @field:Size(min = 4, max = 20, message = "O nome de usuário deve conter entre 4 e 20 caracteres.")
    val username: String,

    @field:NotBlank(message = "O email é obrigatório.")
    @field:Email(message = "Informe um endereço de email válido.")
    val email: String,

    @field:NotBlank(message = "O telefone é obrigatório.")
    val phone: String,

    @field:NotBlank(message = "A senha é obrigatória.")
    @field:Size(min = 6, max = 30, message = "A senha deve conter entre 6 e 30 caracteres.")
    val password: String,
)

data class SignInDto(
    @field:NotBlank(message = "O login é obrigatório.")
    val login: String,

    @field:NotBlank(message = "A senha é obrigatória.")
    val password: String
)