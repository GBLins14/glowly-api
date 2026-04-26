package com.glowly.identity.dto

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiErrorResponse(
    val success: Boolean = false,
    val status: Int,
    val error: String,
    val message: String,
    val fieldErrors: Map<String, String>? = null,
    val timestamp: Instant = Instant.now()
)
