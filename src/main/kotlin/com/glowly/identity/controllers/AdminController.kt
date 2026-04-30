package com.glowly.identity.controllers

import com.glowly.identity.dto.BanDto
import com.glowly.identity.dto.SetRoleDto
import com.glowly.identity.dto.UserResponse
import com.glowly.identity.enums.Role
import com.glowly.identity.extensions.success
import com.glowly.identity.models.User
import com.glowly.identity.services.AdminService
import com.glowly.identity.utils.MessageConstants
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import kotlin.enums.EnumEntries

@RestController
@RequestMapping("/v1/admin")
class AdminController(
    private val adminService: AdminService
) {
    @GetMapping("/accounts")
    fun getAccounts(@AuthenticationPrincipal adminAccount: User): ResponseEntity<List<UserResponse>> {
        val accounts = adminService.getAllAccounts(adminAccount)
        return ResponseEntity.ok(accounts)
    }

    @GetMapping("/accounts/pendants")
    fun getPendantAccounts(@AuthenticationPrincipal adminAccount: User): ResponseEntity<List<UserResponse>> {
        val accounts = adminService.getPendingAccounts(adminAccount)
        return ResponseEntity.ok(accounts)
    }

    @PatchMapping("/accounts/approve/{targetAccountId}")
    fun approveAccount(@AuthenticationPrincipal adminAccount: User, @PathVariable targetAccountId: Long): ResponseEntity<Any> {
        adminService.approveAccount(adminAccount, targetAccountId)
        return ResponseEntity.ok().success(MessageConstants.Success.ACCOUNT_APPROVED)
    }

    @GetMapping("/accounts/{login}")
    fun getAccount(@AuthenticationPrincipal adminAccount: User, @PathVariable login: String): ResponseEntity<UserResponse> {
        val account = adminService.getAccountByLogin(adminAccount, login)
        return ResponseEntity.ok(account)
    }

    @GetMapping("/roles")
    fun getRoles(): ResponseEntity<EnumEntries<Role>> {
        val roles = adminService.getAllRoles()
        return ResponseEntity.ok(roles)
    }

    @PatchMapping("/accounts/role")
    fun setRole(@Valid @RequestBody request: SetRoleDto, @AuthenticationPrincipal adminAccount: User): ResponseEntity<Any> {
        adminService.updateRole(request, adminAccount)
        return ResponseEntity.ok().success(MessageConstants.Success.ROLE_UPDATED)
    }

    @PatchMapping("/accounts/ban")
    fun banAccount(@Valid @RequestBody request: BanDto, @AuthenticationPrincipal adminAccount: User): ResponseEntity<Any> {
        adminService.banAccount(request, adminAccount)
        return ResponseEntity.ok().success(MessageConstants.Success.ACCOUNT_BANNED)
    }

    @PatchMapping("/accounts/unban/{targetAccountId}")
    fun unbanAccount(@AuthenticationPrincipal adminAccount: User, @PathVariable targetAccountId: Long): ResponseEntity<Any> {
        adminService.unbanAccount(adminAccount, targetAccountId)
        return ResponseEntity.ok().success(MessageConstants.Success.ACCOUNT_UNBANNED)
    }

    @GetMapping("/accounts/bans")
    fun getBans(@AuthenticationPrincipal adminAccount: User): ResponseEntity<List<UserResponse>> {
        val accounts = adminService.getBannedAccounts(adminAccount)
        return ResponseEntity.ok(accounts)
    }

    @DeleteMapping("/accounts/{targetAccountId}")
    fun delAccount(@PathVariable targetAccountId: Long, @AuthenticationPrincipal adminAccount: User): ResponseEntity<Any> {
        adminService.deleteAccount(targetAccountId, adminAccount)
        return ResponseEntity.ok().success(MessageConstants.Success.ACCOUNT_DELETED)
    }
}