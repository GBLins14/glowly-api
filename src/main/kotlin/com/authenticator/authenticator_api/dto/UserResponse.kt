package com.authenticator.authenticator_api.dto

import com.authenticator.authenticator_api.enums.AccountStatus
import com.authenticator.authenticator_api.models.Store
import com.authenticator.authenticator_api.models.User

data class UserResponse(
    val id: Long,
    val storeId: Long?,
    val role: String?,
    val cpf: String,
    val fullName: String?,
    val username: String,
    val email: String,
    val phone: String,
    val accountStatus: AccountStatus
)
fun User.toResponseDTO(): UserResponse {
    return UserResponse(
        id = this.id,
        storeId = this.store?.id,
        role = this.role?.name,
        cpf = this.cpf,
        fullName = this.fullName,
        username = this.username,
        email = this.email,
        phone = this.phone,
        accountStatus = this.accountStatus
    )
}