package com.glowly.identity.security

import com.glowly.identity.enums.AccountStatus
import com.glowly.identity.exceptions.UnauthorizedException
import com.glowly.identity.repositories.AccountRepository
import com.glowly.identity.utils.MessageConstants
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil,
    private val accountRepository: AccountRepository,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = request.getHeader(HttpHeaders.AUTHORIZATION)
            ?.removePrefix("Bearer ")
            ?.trim()

        if (token.isNullOrBlank() || !jwtUtil.validateToken(token)) {
            filterChain.doFilter(request, response)
            return
        }

        val username = jwtUtil.getUsername(token)
        val user = accountRepository.findByUsername(username)

        if (user == null) {
            filterChain.doFilter(request, response)
            return
        }

        val tokenVersion = jwtUtil.getTokenVersion(token)
        if (tokenVersion != user.tokenVersion) {
            throw UnauthorizedException(MessageConstants.Error.INVALID_CREDENTIALS)
        }

        if (user.accountStatus == AccountStatus.PENDING) {
            throw UnauthorizedException(MessageConstants.Error.ACCOUNT_PENDING)
        }

        if (user.banned) {
            if (user.banExpiresAt == null) {
                throw UnauthorizedException(MessageConstants.Error.ACCOUNT_BANNED_PERMANENT)
            }

            if (!user.isBanExpired()) {
                throw UnauthorizedException(MessageConstants.Error.ACCOUNT_BANNED_TEMPORARY)
            }

            user.apply {
                banned = false
                bannedAt = null
                banExpiresAt = null
                failedLoginAttempts = 0
            }
            accountRepository.save(user)
        }

        if (user.store != null && !user.store!!.active) {
            throw UnauthorizedException(MessageConstants.Error.STORE_INACTIVE)
        }

        val authorities = listOf(SimpleGrantedAuthority("ROLE_${user.role?.name?.uppercase()}"))
        val authentication = UsernamePasswordAuthenticationToken(
            user,
            null,
            authorities
        )
        SecurityContextHolder.getContext().authentication = authentication

        filterChain.doFilter(request, response)
    }
}