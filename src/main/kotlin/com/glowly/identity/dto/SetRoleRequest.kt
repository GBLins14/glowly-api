package com.glowly.identity.dto

import com.glowly.identity.enums.Role
import jakarta.validation.constraints.NotNull

data class SetRoleDto(
    @field:NotNull(message = "O ID da conta é obrigatório.")
    val id: Long,

    @field:NotNull(message = "O cargo é obrigatório.")
    val role: Role
)