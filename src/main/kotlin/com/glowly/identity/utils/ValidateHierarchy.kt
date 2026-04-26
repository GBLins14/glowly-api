package com.glowly.identity.utils

import com.glowly.identity.enums.Role
import com.glowly.identity.exceptions.ConflictException
import com.glowly.identity.exceptions.ForbiddenException
import com.glowly.identity.models.User

fun validateHierarchy(user: User, targetAccount: User) {
    if (targetAccount == user) {
        throw ConflictException("Você não pode editar sua própria conta enquanto logado.")
    }

    if (user.role == Role.ADMIN && targetAccount.role == Role.ADMIN) {
        throw ForbiddenException("Você não tem permissão para gerenciar um usuário com cargo igual ou superior ao seu.")
    }
}