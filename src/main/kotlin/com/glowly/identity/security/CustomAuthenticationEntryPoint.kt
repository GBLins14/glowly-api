package com.glowly.identity.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.glowly.identity.dto.ApiErrorResponse
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class CustomAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper
) : AuthenticationEntryPoint {

    @Throws(IOException::class, ServletException::class)
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        val errorResponse = ApiErrorResponse(
            status = HttpStatus.UNAUTHORIZED.value(),
            error = "Não autorizado",
            message = authException.message ?: "Autenticação necessária para acessar este recurso."
        )

        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }
}

