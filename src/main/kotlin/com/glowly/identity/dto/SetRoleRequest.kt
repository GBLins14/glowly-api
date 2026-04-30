package com.glowly.identity.dto

import com.glowly.identity.enums.Role
import com.glowly.identity.utils.MessageConstants
import jakarta.validation.constraints.NotNull

data class SetRoleDto(
    @field:NotNull(message = MessageConstants.Error.INVALID_ID)
    val id: Long,

    @field:NotNull(message = MessageConstants.Error.INVALID_ROLE)
    val role: Role
)