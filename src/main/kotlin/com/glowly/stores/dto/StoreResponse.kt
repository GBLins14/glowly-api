package com.glowly.stores.dto

import com.glowly.stores.models.Store

data class StoreResponse (
    val id: Long? = null,
    val ownerId: Long? = null,
    val cnpj: String? = null,
    val name: String? = null,
    val description: String? = null,
    val street: String? = null,
    val number: String? = null,
    val complement: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zipCode: String? = null,
    val country: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val website: String? = null
)
fun Store.toResponseDTO(): StoreResponse {
    return StoreResponse (
        id = this.id,
        ownerId = this.owner.id,
        cnpj = this.cnpj,
        name = this.name,
        description = this.description,
        street = this.street,
        number = this.number,
        complement = this.complement,
        city = this.city,
        state = this.state,
        zipCode = this.zipCode,
        country = this.country,
        email = this.email,
        phone = this.phone,
        website = this.website
    )
}