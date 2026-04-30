package com.glowly.identity.dto

import com.glowly.identity.utils.MessageConstants
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class ForgotPasswordRequest(
    @field:NotBlank(message = MessageConstants.Error.INVALID_EMAIL)
    @field:Email(message = MessageConstants.Error.INVALID_EMAIL)
    val email: String
)

data class ResetPasswordRequest(
    @field:NotBlank(message = MessageConstants.Error.TOKEN_NOT_FOUND)
    val token: String,

    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{6,30}\$",
        message = MessageConstants.Error.INVALID_PASSWORD_LENGTH
    )
    @field:NotBlank(message = MessageConstants.Error.INVALID_PASSWORD_LENGTH)
    val newPassword: String
)