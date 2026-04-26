package com.glowly.identity.utils

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*

fun generateToken(): String {
    val random = SecureRandom()
    val bytes = ByteArray(32)
    random.nextBytes(bytes)
    val token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)

    return token
}

fun hashToken(token: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val hashBytes = digest.digest(token.toByteArray(Charsets.UTF_8))
    return Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes)
}