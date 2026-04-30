package com.glowly.identity.dto

import com.glowly.identity.enums.Role
import com.glowly.identity.utils.MessageConstants
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.br.CPF

data class SignUpDto(
    val storeId: Long? = null,
    val role: Role? = null,

    @field:NotBlank(message = MessageConstants.Error.INVALID_CPF)
    @field:CPF(message = MessageConstants.Error.INVALID_CPF)
    val cpf: String,

    @field:NotBlank(message = MessageConstants.Error.INVALID_FULLNAME)
    @field:Size(min = 5, max = 120, message = MessageConstants.Error.INVALID_FULLNAME)
    val fullName: String,

    @field:NotBlank(message = MessageConstants.Error.INVALID_USERNAME_LENGTH)
    @field:Size(min = 4, max = 20, message = MessageConstants.Error.INVALID_USERNAME_LENGTH)
    @field:Pattern(
        regexp = "^[a-z0-9_]+$",
        message = MessageConstants.Error.INVALID_USERNAME_PATTERN
    )
    val username: String,

    @field:Size(max = 254, message = MessageConstants.Error.INVALID_EMAIL)
    @field:NotBlank(message = MessageConstants.Error.INVALID_EMAIL)
    @field:Email(message = MessageConstants.Error.INVALID_EMAIL)
    val email: String,

    @field:Size(max = 20, message = MessageConstants.Error.INVALID_PHONE)
    @field:NotBlank(message = MessageConstants.Error.INVALID_PHONE)
    val phone: String,

    @field:Pattern(
        regexp = "^[A-Za-z0-9\\W_]{6,30}$",
        message = MessageConstants.Error.INVALID_PASSWORD_LENGTH
    )
    @field:NotBlank(message = MessageConstants.Error.INVALID_PASSWORD_LENGTH)
    val password: String,
)

data class SignInDto(
    @field:NotBlank(message = MessageConstants.Error.INVALID_LOGIN)
    val login: String,

    @field:NotBlank(message = MessageConstants.Error.INVALID_CREDENTIALS)
    val password: String,
)