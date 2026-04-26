package com.glowly.identity.security

import com.glowly.identity.enums.Role
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.*

@Component
class JwtUtil(
    @Value("\${jwt.jwt-secret}") private val secret: String,
    @Value("\${jwt.jwt-expiration-days}") private val expirationDays: Long
) {
    private val key = Keys.hmacShaKeyFor(secret.toByteArray())

    fun generateToken(username: String, role: Role?, tokenVersion: Int): String {
        val now = Date()
        val expiry = Date(now.time + Duration.ofDays(expirationDays).toMillis())

        return Jwts.builder()
            .subject(username)
            .claim("tokenVersion", tokenVersion)
            .claim("role", role?.name)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            getClaims(token)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun getUsername(token: String): String {
        return getClaims(token).subject
    }

    fun getTokenVersion(token: String): Int {
        return getClaims(token)["tokenVersion"] as Int
    }

    private fun getClaims(token: String) =
        Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
}