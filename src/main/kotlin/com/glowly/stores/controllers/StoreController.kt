package com.glowly.stores.controllers

import com.glowly.identity.extensions.success
import com.glowly.stores.dto.CreateStoreDto
import com.glowly.stores.services.StoreService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/store")
class StoreController(
    private val storeService: StoreService
) {
    @PostMapping("/create")
    fun createStore(@RequestBody request: CreateStoreDto): ResponseEntity<Any> {
        val messageReturn = storeService.createStore(request)
        return ResponseEntity.status(HttpStatus.OK).success(messageReturn)
    }

    @PutMapping("/update")
    fun updateStore(@RequestBody request: CreateStoreDto): ResponseEntity<Any> {
        val messageReturn = storeService.updateStore(request)
        return ResponseEntity.status(HttpStatus.OK).success(messageReturn)
    }

    @GetMapping("/get")
    fun getStore(@RequestBody request: CreateStoreDto): ResponseEntity<Any> {
        val messageReturn = storeService.getStore(request)
        return ResponseEntity.status(HttpStatus.OK).success(messageReturn)
    }

    @DeleteMapping("/delete")
    fun deleteStore(@RequestBody request: CreateStoreDto): ResponseEntity<Any> {
        val messageReturn = storeService.deleteStore(request)
        return ResponseEntity.status(HttpStatus.OK).success(messageReturn)
    }
}