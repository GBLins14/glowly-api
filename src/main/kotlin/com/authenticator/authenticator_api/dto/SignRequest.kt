package com.authenticator.authenticator_api.dto

import com.authenticator.authenticator_api.enums.Role

data class SignUpDto(
    val storeId: Long? = null,
    val role: Role? = null,
    val cpf: String,
    val fullName: String,
    val username: String,
    val email: String,
    val phone: String,
    val password: String,
)

data class SignInDto(
    val login: String,
    val password: String
)