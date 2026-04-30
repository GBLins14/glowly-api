package com.glowly.stores.repositories

import com.glowly.identity.models.User
import com.glowly.stores.models.Store
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StoreRepository : JpaRepository<Store, Long> {
    fun findByOwner(owner: User): Store?
    fun findByCnpj(cnpj: String): Store?
    fun findByName(name: String): Store?
    fun findByEmail(email: String): Store?
    fun findByPhone(phone: String): Store?
    fun findByWebsite(website: String): Store?
}