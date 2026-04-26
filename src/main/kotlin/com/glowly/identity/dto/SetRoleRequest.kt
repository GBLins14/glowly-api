package com.glowly.identity.dto

import com.glowly.identity.enums.Role

data class SetRoleDto(
    val id: Long,
    val role: Role
)