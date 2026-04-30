package com.glowly.identity.utils

import com.glowly.identity.enums.Role
import com.glowly.identity.exceptions.ConflictException
import com.glowly.identity.exceptions.ForbiddenException
import com.glowly.identity.models.User

fun validateHierarchy(user: User, targetAccount: User) {
    if (targetAccount == user) {
        throw ConflictException(MessageConstants.Error.SELF_MANAGEMENT)
    }

    if (user.role == Role.ADMIN && targetAccount.role == Role.ADMIN) {
        throw ForbiddenException(MessageConstants.Error.HIERARCHY_VIOLATION)
    }
}