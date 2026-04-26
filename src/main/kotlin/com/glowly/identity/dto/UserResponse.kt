package com.glowly.identity.dto

import com.glowly.identity.enums.AccountStatus
import com.glowly.identity.enums.Role
import com.glowly.identity.models.User

data class UserResponse(
    val id: Long,
    val storeId: Long?,
    val role: Role?,
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
        role = this.role,
        cpf = this.cpf,
        fullName = this.fullName,
        username = this.username,
        email = this.email,
        phone = this.phone,
        accountStatus = this.accountStatus
    )
}