package com.glowly.glowly_api.controllers

import com.glowly.glowly_api.annotations.CurrentUser
import com.glowly.glowly_api.dto.*
import com.glowly.glowly_api.extensions.success
import com.glowly.glowly_api.models.User
import com.glowly.glowly_api.services.AuthService
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/sign-up")
    @SecurityRequirements
    fun signUp(@RequestBody request: SignUpDto): ResponseEntity<Any> {
        val messageReturn = authService.register(request)
        return ResponseEntity.status(HttpStatus.OK).success(messageReturn)
    }

    @PostMapping("/sign-in")
    @SecurityRequirements
    fun signIn(@RequestBody request: SignInDto): ResponseEntity<Any> {
        val token = authService.login(request)
        return ResponseEntity.ok(mapOf("success" to true, "token" to token))
    }

    @PostMapping("/forgot-password")
    @SecurityRequirements
    fun forgotPassword(@RequestBody request: ForgotPasswordRequest): ResponseEntity<Any> {
        authService.processForgotPassword(request.email)
        return ResponseEntity.status(HttpStatus.OK).success("Se o e-mail estiver cadastrado, você receberá um link de recuperação.")
    }

    @PostMapping("/reset-password")
    fun resetPassword(@RequestBody request: ResetPasswordRequest): ResponseEntity<Any> {
        authService.processResetPassword(request.token, request.newPassword)
        return ResponseEntity.status(HttpStatus.OK).success("Sua senha foi alterada com sucesso! Você já pode fazer login.")
    }

    @PostMapping("/logout")
    fun logout(@CurrentUser user: User): ResponseEntity<Any> {
        authService.logout(user)
        return ResponseEntity.status(HttpStatus.OK).success("Logout realizado com sucesso.")
    }

    @GetMapping("/me")
    fun me(@CurrentUser user: User): ResponseEntity<UserResponse> {
        val me = authService.getMe(user)
        return ResponseEntity.ok(me)
    }
}