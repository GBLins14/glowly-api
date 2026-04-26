package com.glowly.identity.dto

import jakarta.validation.constraints.NotNull
import java.time.temporal.ChronoUnit

data class BanDto(
    @field:NotNull(message = "O ID da conta é obrigatório.")
    val id: Long,

    val duration: Long? = null,
    val unit: ChronoUnit? = null
)