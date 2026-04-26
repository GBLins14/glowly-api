package com.glowly.identity.repositories

import com.glowly.identity.models.PasswordResetToken
import com.glowly.identity.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TokenRepository : JpaRepository<PasswordResetToken, UUID> {
    fun findByTokenHash(tokenHash: String): PasswordResetToken?
    fun deleteByUser(user: User)
}