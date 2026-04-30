package com.glowly.identity.dto

import com.glowly.identity.utils.MessageConstants
import jakarta.validation.constraints.NotNull
import java.time.temporal.ChronoUnit

data class BanDto(
    @field:NotNull(message = MessageConstants.Error.INVALID_ID)
    val id: Long,

    val duration: Long? = null,
    val unit: ChronoUnit? = null
)