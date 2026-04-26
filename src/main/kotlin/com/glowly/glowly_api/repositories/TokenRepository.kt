package com.glowly.glowly_api.repositories

import com.glowly.glowly_api.models.PasswordResetToken
import com.glowly.glowly_api.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TokenRepository : JpaRepository<PasswordResetToken, UUID> {
    fun findByToken(token: String): PasswordResetToken?
    fun deleteByUser(user: User)
}