package com.glowly.identity.exceptions

import com.glowly.identity.dto.ApiErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class UserGlobalExceptionHandler {

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(e: BadRequestException): ResponseEntity<ApiErrorResponse> {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Requisição inválida", e.message)
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(e: NotFoundException): ResponseEntity<ApiErrorResponse> {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Recurso não encontrado", e.message)
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorized(e: UnauthorizedException): ResponseEntity<ApiErrorResponse> {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Não autorizado", e.message)
    }

    @ExceptionHandler(ForbiddenException::class)
    fun handleForbidden(e: ForbiddenException): ResponseEntity<ApiErrorResponse> {
        return buildErrorResponse(HttpStatus.FORBIDDEN, "Acesso negado", e.message)
    }

    @ExceptionHandler(ConflictException::class)
    fun handleConflict(e: ConflictException): ResponseEntity<ApiErrorResponse> {
        return buildErrorResponse(HttpStatus.CONFLICT, "Conflito de dados", e.message)
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(e: AuthenticationException): ResponseEntity<ApiErrorResponse> {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Falha na autenticação", e.message)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<ApiErrorResponse> {
        val fieldErrors = e.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "Campo inválido.")
        }
        val response = ApiErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Erro de validação",
            message = "Um ou mais campos estão inválidos. Verifique os dados informados.",
            fieldErrors = fieldErrors
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneric(e: Exception): ResponseEntity<ApiErrorResponse> {
        return buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Erro interno",
            "Ocorreu um erro inesperado. Tente novamente mais tarde."
        )
    }

    private fun buildErrorResponse(
        status: HttpStatus,
        error: String,
        message: String?
    ): ResponseEntity<ApiErrorResponse> {
        val response = ApiErrorResponse(
            status = status.value(),
            error = error,
            message = message ?: "Ocorreu um erro."
        )
        return ResponseEntity.status(status).body(response)
    }
}