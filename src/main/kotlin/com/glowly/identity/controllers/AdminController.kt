package com.glowly.identity.controllers

import com.glowly.identity.annotations.CurrentUser
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
import org.springframework.web.bind.annotation.*
import kotlin.enums.EnumEntries

@RestController
@RequestMapping("/v1/admin")
class AdminController(
    private val adminService: AdminService
) {
    @GetMapping("/accounts")
    fun getAccounts(): ResponseEntity<List<UserResponse>> {
        val accounts = adminService.getAllAccounts()
        return ResponseEntity.ok(accounts)
    }

    @GetMapping("/accounts/pendants")
    fun getPendantAccounts(): ResponseEntity<List<UserResponse>> {
        val accounts = adminService.getPendingAccounts()
        return ResponseEntity.ok(accounts)
    }

    @PatchMapping("/accounts/approve/{accountId}")
    fun approveAccount(@PathVariable accountId: Long): ResponseEntity<Any> {
        adminService.approveAccount(accountId)
        return ResponseEntity.ok().success(MessageConstants.Success.ACCOUNT_APPROVED)
    }

    @GetMapping("/accounts/{login}")
    fun getAccount(@PathVariable login: String): ResponseEntity<UserResponse> {
        val account = adminService.getAccountByLogin(login)
        return ResponseEntity.ok(account)
    }

    @GetMapping("/roles")
    fun getRoles(): ResponseEntity<EnumEntries<Role>> {
        val roles = adminService.getAllRoles()
        return ResponseEntity.ok(roles)
    }

    @PatchMapping("/accounts/role")
    fun setRole(@Valid @RequestBody request: SetRoleDto, @CurrentUser user: User): ResponseEntity<Any> {
        adminService.updateRole(request, user)
        return ResponseEntity.ok().success(MessageConstants.Success.ROLE_UPDATED)
    }

    @PatchMapping("/accounts/ban")
    fun banAccount(@Valid @RequestBody request: BanDto, @CurrentUser user: User): ResponseEntity<Any> {
        adminService.banAccount(request, user)
        return ResponseEntity.ok().success(MessageConstants.Success.ACCOUNT_BANNED)
    }

    @PatchMapping("/accounts/unban/{accountId}")
    fun unbanAccount(@PathVariable accountId: Long): ResponseEntity<Any> {
        adminService.unbanAccount(accountId)
        return ResponseEntity.ok().success(MessageConstants.Success.ACCOUNT_UNBANNED)
    }

    @GetMapping("/accounts/bans")
    fun getBans(): ResponseEntity<List<UserResponse>> {
        val accounts = adminService.getBannedAccounts()
        return ResponseEntity.ok(accounts)
    }

    @DeleteMapping("/accounts/{accountId}")
    fun delAccount(@PathVariable accountId: Long, @CurrentUser user: User): ResponseEntity<Any> {
        adminService.deleteAccount(accountId, user)
        return ResponseEntity.ok().success(MessageConstants.Success.ACCOUNT_DELETED)
    }
}