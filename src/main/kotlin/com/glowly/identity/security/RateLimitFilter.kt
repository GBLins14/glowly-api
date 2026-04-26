package com.glowly.identity.security

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Component
class RateLimitFilter(
    @Value("\${app.max-requests-per-minutes}") private val maxRequestsPerMinutes: Long,
    @Value("\${app.trusted-proxies:}") private val trustedProxiesRaw: String,
) : Filter {
    private val log: Logger = LoggerFactory.getLogger(javaClass)
    private val cache = ConcurrentHashMap<String, Bucket>()

    private val protectedPaths = listOf(
        "/api/"
    )

    private val trustedProxies: Set<String> by lazy {
        trustedProxiesRaw.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .toSet()
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse
        val path = httpRequest.requestURI

        if (path.startsWith("/api/webhooks")) {
            chain.doFilter(request, response)
            return
        }

        val isProtected = protectedPaths.any { path.startsWith(it) }

        if (!isProtected) {
            chain.doFilter(request, response)
            return
        }

        val clientIp = getClientIp(httpRequest)
        val bucket = cache.computeIfAbsent(clientIp) { createNewBucket() }

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response)
        } else {
            log.warn("Rate limit excedido para IP: {}", clientIp)
            httpResponse.status = HttpStatus.TOO_MANY_REQUESTS.value()
            httpResponse.writer.write("Muitas requisições. Aguarde um minuto antes de tentar novamente.")
        }
    }

    private fun createNewBucket(): Bucket {
        val limit = Bandwidth.builder()
            .capacity(maxRequestsPerMinutes)
            .refillGreedy(maxRequestsPerMinutes, Duration.ofMinutes(1))
            .build()
        return Bucket.builder().addLimit(limit).build()
    }

    private fun getClientIp(request: HttpServletRequest): String {
        val remoteAddr = request.remoteAddr
        if (trustedProxies.isNotEmpty() && remoteAddr in trustedProxies) {
            val xForwardedFor = request.getHeader("X-Forwarded-For")
            if (!xForwardedFor.isNullOrBlank()) {
                return xForwardedFor.split(",")[0].trim()
            }
        }
        return remoteAddr
    }
}