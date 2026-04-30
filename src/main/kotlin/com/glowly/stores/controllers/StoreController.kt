package com.glowly.stores.controllers

import com.glowly.identity.extensions.success
import com.glowly.identity.models.User
import com.glowly.identity.utils.MessageConstants
import com.glowly.stores.dto.CreateStoreDto
import com.glowly.stores.dto.StoreResponse
import com.glowly.stores.dto.UpdateStoreDto
import com.glowly.stores.services.StoreService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/store")
class StoreController(
    private val storeService: StoreService
) {
    @PostMapping("/create")
    fun createStore(@Valid @RequestBody request: CreateStoreDto, @AuthenticationPrincipal user: User): ResponseEntity<Any> {
        storeService.createStore(request, user)
        return ResponseEntity.status(HttpStatus.OK).success(MessageConstants.Success.STORE_CREATED)
    }

    @PutMapping("/update")
    fun updateStore(@Valid @RequestBody request: UpdateStoreDto, @AuthenticationPrincipal user: User): ResponseEntity<Any> {
        storeService.updateStore(request, user)
        return ResponseEntity.status(HttpStatus.OK).success(MessageConstants.Success.STORE_UPDATED)
    }

    @GetMapping("/get")
    fun getStore(@AuthenticationPrincipal user: User): ResponseEntity<StoreResponse> {
        val store = storeService.getStore(user)
        return ResponseEntity.ok(store)
    }

    @DeleteMapping("/delete")
    fun deleteStore(@AuthenticationPrincipal user: User): ResponseEntity<Any> {
        storeService.deleteStore(user)
        return ResponseEntity.status(HttpStatus.OK).success(MessageConstants.Success.STORE_DELETED)
    }
}