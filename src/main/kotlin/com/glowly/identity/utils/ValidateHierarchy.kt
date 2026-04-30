package com.glowly.identity.utils

import com.glowly.identity.enums.Role
import com.glowly.identity.exceptions.ConflictException
import com.glowly.identity.exceptions.ForbiddenException
import com.glowly.identity.models.User

fun validateHierarchy(adminAccount: User, targetAccount: User) {
    if (adminAccount.store!!.owner == adminAccount) return

    if (targetAccount == adminAccount) {
        throw ConflictException(MessageConstants.Error.SELF_MANAGEMENT)
    }

    if (adminAccount.role == Role.ADMIN && targetAccount.role == Role.ADMIN) {
        throw ForbiddenException(MessageConstants.Error.HIERARCHY_VIOLATION)
    }
}