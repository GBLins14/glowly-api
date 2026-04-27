package com.glowly.stores.dto

import com.glowly.identity.utils.MessageConstants
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.URL
import org.hibernate.validator.constraints.br.CNPJ

data class CreateStoreDto(
    @field:CNPJ(message = MessageConstants.Error.INVALID_CNPJ)
    val cnpj: String? = null,

    @field:Size(min = 4, max = 50, message = MessageConstants.Error.INVALID_STORE_NAME)
    @field:NotBlank(message = MessageConstants.Error.INVALID_STORE_NAME)
    val name: String,

    @field:Size(max = 255, message = MessageConstants.Error.INVALID_STORE_DESCRIPTION)
    val description: String? = null,

    @field:Size(max = 50, message = MessageConstants.Error.INVALID_STREET)
    @field:NotBlank(message = MessageConstants.Error.INVALID_STREET)
    val street: String,

    @field:Size(max = 10, message = MessageConstants.Error.INVALID_STORE_NUMBER)
    @field:NotBlank(message = MessageConstants.Error.INVALID_STORE_NUMBER)
    val number: String,

    @field:Size(max = 50, message = MessageConstants.Error.INVALID_STORE_COMPLEMENT)
    val complement: String? = null,

    @field:Size(max = 50, message = MessageConstants.Error.INVALID_STORE_CITY)
    @field:NotBlank(message = MessageConstants.Error.INVALID_STORE_CITY)
    val city: String,

    @field:Size(max = 50, message = MessageConstants.Error.INVALID_STORE_STATE)
    @field:NotBlank(message = MessageConstants.Error.INVALID_STORE_STATE)
    val state: String,

    @field:Size(max = 10, message = MessageConstants.Error.INVALID_STORE_ZIP_CODE)
    @field:NotBlank(message = MessageConstants.Error.INVALID_STORE_ZIP_CODE)
    val zipCode: String,

    @field:NotBlank(message = MessageConstants.Error.INVALID_EMAIL)
    @field:Email(message = MessageConstants.Error.INVALID_EMAIL)
    val email: String,

    @field:Size(max = 15, message = MessageConstants.Error.INVALID_PHONE)
    @field:NotBlank(message = MessageConstants.Error.INVALID_PHONE)
    @field:Pattern(regexp = "^[0-9]+$", message = MessageConstants.Error.INVALID_PHONE)
    val phone: String,

    @field:Size(max = 100, message = MessageConstants.Error.INVALID_STORE_WEBSITE)
    @field:URL
    val website: String? = null
)

data class UpdateStoreDataDto(
    val name: String? = null,
    val description: String? = null,
    val street: String? = null,
    val number: String? = null,
    val complement: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zipCode: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val website: String? = null
)
