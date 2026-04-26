package com.glowly.glowly_api.dto

import com.glowly.glowly_api.enums.Role

data class SetRoleDto(
    val id: Long,
    val role: Role
)