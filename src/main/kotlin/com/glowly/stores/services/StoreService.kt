package com.glowly.stores.services

import com.glowly.identity.services.ValidatorService
import com.glowly.stores.dto.CreateStoreDto
import org.springframework.transaction.annotation.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class StoreService(
    private val validatorService: ValidatorService
) {
    private val logger = LoggerFactory.getLogger(StoreService::class.java)

    @Transactional
    fun createStore(request: CreateStoreDto): String {
        return "Loja criada com sucesso!"
    }

    @Transactional
    fun updateStore(request: CreateStoreDto): String {
        return "Loja atualizada com sucesso!"
    }

    @Transactional
    fun getStore(request: CreateStoreDto): String {
        return "Loja obtida com sucesso!"
    }

    @Transactional
    fun deleteStore(request: CreateStoreDto): String {
        return "Loja deletada com sucesso!"
    }
}