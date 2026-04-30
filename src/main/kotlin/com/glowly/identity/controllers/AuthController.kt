package com.glowly.identity.controllers

import com.glowly.identity.dto.*
import com.glowly.identity.extensions.success
import com.glowly.identity.models.User
import com.glowly.identity.services.AuthService
import com.glowly.identity.utils.MessageConstants
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/sign-up")
    @SecurityRequirements
    fun signUp(@Valid @RequestBody request: SignUpDto): ResponseEntity<Any> {
        val message = authService.register(request)
        return ResponseEntity.status(HttpStatus.CREATED).success(message)
    }

    @PostMapping("/sign-in")
    @SecurityRequirements
    fun signIn(@Valid @RequestBody request: SignInDto): ResponseEntity<Any> {
        val token = authService.login(request)
        return ResponseEntity.ok(mapOf("success" to true, "token" to token))
    }

    @PostMapping("/forgot-password")
    @SecurityRequirements
    fun forgotPassword(@Valid @RequestBody request: ForgotPasswordRequest): ResponseEntity<Any> {
        authService.processForgotPassword(request.email)
        return ResponseEntity.status(HttpStatus.OK).success(MessageConstants.Success.PASSWORD_RECOVERY_SENT)
    }

    @PostMapping("/reset-password")
    fun resetPassword(@Valid @RequestBody request: ResetPasswordRequest): ResponseEntity<Any> {
        authService.processResetPassword(request.token, request.newPassword)
        return ResponseEntity.status(HttpStatus.OK).success(MessageConstants.Success.PASSWORD_CHANGED)
    }

    @PostMapping("/logout")
    fun logout(@AuthenticationPrincipal user: User): ResponseEntity<Any> {
        authService.logout(user)
        return ResponseEntity.status(HttpStatus.OK).success(MessageConstants.Success.LOGOUT)
    }

    @GetMapping("/me")
    fun me(@AuthenticationPrincipal user: User): ResponseEntity<UserResponse> {
        val me = authService.getMe(user)
        return ResponseEntity.ok(me)
    }
}