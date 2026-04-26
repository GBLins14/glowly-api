package com.glowly.stores.repositories

import com.glowly.stores.models.Store
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StoreRepository : JpaRepository<Store, Long> {
    fun findByCnpj(cnpj: String): Store?
    fun findByEmail(email: String): Store?
    fun findByPhone(phone: String): Store?
}