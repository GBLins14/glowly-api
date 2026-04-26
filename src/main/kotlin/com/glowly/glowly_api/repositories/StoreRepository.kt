package com.glowly.glowly_api.repositories

import com.glowly.glowly_api.models.Store
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StoreRepository : JpaRepository<Store, Long> {
    fun findByCnpj(cnpj: String): Store?
    fun findByEmail(email: String): Store?
    fun findByPhone(phone: String): Store?
}