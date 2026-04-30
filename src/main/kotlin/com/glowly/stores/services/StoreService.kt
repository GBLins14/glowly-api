package com.glowly.stores.services

import com.glowly.identity.enums.Role
import com.glowly.identity.exceptions.ConflictException
import com.glowly.identity.exceptions.ForbiddenException
import com.glowly.identity.exceptions.NotFoundException
import com.glowly.identity.models.User
import com.glowly.identity.repositories.AccountRepository
import com.glowly.identity.utils.MessageConstants
import com.glowly.stores.dto.CreateStoreDto
import com.glowly.stores.dto.StoreResponse
import com.glowly.stores.dto.UpdateStoreDto
import com.glowly.stores.dto.toResponseDTO
import com.glowly.stores.models.Store
import com.glowly.stores.repositories.StoreRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StoreService(
    private val storeRepository: StoreRepository,
    private val accountRepository: AccountRepository
) {
    private val logger = LoggerFactory.getLogger(StoreService::class.java)

    @Transactional
    fun createStore(request: CreateStoreDto, owner: User) {
        request.cnpj?.let { cnpj ->
            cnpj.replace(Regex("[^0-9]"), "")
            val existingStoreByCnpj = storeRepository.findByCnpj(cnpj)
            if (existingStoreByCnpj != null) {
                throw ConflictException(MessageConstants.Error.INVALID_STORE_CNPJ_ASSIGNED)
            }
        }

        val existingStoreByOwner = storeRepository.findByOwner(owner)
        if (existingStoreByOwner != null || owner.store != null) {
            throw ConflictException(MessageConstants.Error.INVALID_STORE_OWNER_ASSIGNED)
        }

        val existingStoreByName = storeRepository.findByName(request.name)
        if (existingStoreByName != null) {
            throw ConflictException(MessageConstants.Error.INVALID_STORE_NAME_ASSIGNED)
        }

        val existingStoreByEmail = storeRepository.findByEmail(request.email)
        if (existingStoreByEmail != null) {
            throw ConflictException(MessageConstants.Error.INVALID_STORE_EMAIL_ASSIGNED)
        }

        val existingStoreByPhone = storeRepository.findByPhone(request.phone)
        if (existingStoreByPhone != null) {
            throw ConflictException(MessageConstants.Error.INVALID_STORE_PHONE_ASSIGNED)
        }

        request.website?.let { website ->
            val existingStoreByWebsite = storeRepository.findByWebsite(website)
            if (existingStoreByWebsite != null) {
                throw ConflictException(MessageConstants.Error.INVALID_STORE_WEBSITE)
            }
        }

        val store = Store(
            owner = owner,
            cnpj = request.cnpj,
            name = request.name,
            description = request.description,
            street = request.street,
            number = request.number,
            complement = request.complement,
            city = request.city,
            state = request.state,
            zipCode = request.zipCode,
            email = request.email,
            phone = request.phone,
            website = request.website
        )

        logger.info("Creating store: ${store.name}")

        val savedStore = storeRepository.save(store)
        owner.assignStore(savedStore, Role.ADMIN)
        accountRepository.save(owner)
    }

    @Transactional
    fun updateStore(request: UpdateStoreDto, user: User) {
        val store = user.store ?: throw NotFoundException(MessageConstants.Error.STORE_NOT_FOUND_USER)

        if (user.role != Role.ADMIN) {
            throw ForbiddenException(MessageConstants.Error.UNAUTHORIZED)
        }

        request.name?.let { name ->
            val existingByName = storeRepository.findByName(name)
            if (existingByName != null && existingByName.id != store.id) {
                throw ConflictException(MessageConstants.Error.INVALID_STORE_NAME_ASSIGNED)
            }
        }

        request.email?.let { email ->
            val existingByEmail = storeRepository.findByEmail(email)
            if (existingByEmail != null && existingByEmail.id != store.id) {
                throw ConflictException(MessageConstants.Error.INVALID_STORE_EMAIL_ASSIGNED)
            }
        }

        request.phone?.let { phone ->
            val existingByPhone = storeRepository.findByPhone(request.phone)
            if (existingByPhone != null && existingByPhone.id != store.id) {
                throw ConflictException(MessageConstants.Error.INVALID_STORE_PHONE_ASSIGNED)
            }
        }

        request.website?.let { website ->
            val existingByWebsite = storeRepository.findByWebsite(website)
            if (existingByWebsite != null && existingByWebsite.id != store.id) {
                throw ConflictException(MessageConstants.Error.INVALID_STORE_WEBSITE)
            }
        }

        store.updateDetails(
            name = request.name,
            description = request.description,
            street = request.street,
            number = request.number,
            complement = request.complement,
            city = request.city,
            state = request.state,
            zipCode = request.zipCode,
            country = null,
            email = request.email,
            phone = request.phone,
            website = request.website
        )

        logger.info("Updating store: ${store.name}")

        storeRepository.save(store)
    }

    @Transactional(readOnly = true)
    fun getStore(user: User): StoreResponse {
        return user.store?.let { it.toResponseDTO() } ?: throw NotFoundException(MessageConstants.Error.STORE_NOT_FOUND_USER)
    }

    @Transactional
    fun deleteStore(user: User) {
        val store = user.store ?: throw NotFoundException(MessageConstants.Error.STORE_NOT_FOUND_USER)

        if (store.owner != user) {
            throw ForbiddenException(MessageConstants.Error.UNAUTHORIZED)
        }

        val associatedUsers = accountRepository.findByStore(store)
        if (associatedUsers.isNotEmpty()) {
            logger.info("Found ${associatedUsers.size} user accounts associated with store: ${store.name}")
        }

        logger.info("Deleting store: ${store.name}")

        storeRepository.delete(store)
        
        if (associatedUsers.isNotEmpty()) {
            logger.info("Deleting ${associatedUsers.size} user accounts associated with store: ${store.name}")
            accountRepository.deleteAll(associatedUsers)
        }
    }
}