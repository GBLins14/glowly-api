package com.glowly.identity.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ForgotPasswordRequest(
    @field:NotBlank(message = "O email é obrigatório.")
    @field:Email(message = "Informe um endereço de email válido.")
    val email: String
)

data class ResetPasswordRequest(
    @field:NotBlank(message = "O token é obrigatório.")
    val token: String,

    @field:NotBlank(message = "A nova senha é obrigatória.")
    @field:Size(min = 6, max = 30, message = "A senha deve conter entre 6 e 30 caracteres.")
    val newPassword: String
)