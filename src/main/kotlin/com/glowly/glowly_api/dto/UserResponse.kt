package com.glowly.glowly_api.dto

import com.glowly.glowly_api.enums.AccountStatus
import com.glowly.glowly_api.enums.Role
import com.glowly.glowly_api.models.Store
import com.glowly.glowly_api.models.User

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