package com.glowly.glowly_api.utils

import com.glowly.glowly_api.exceptions.ConflictException
import com.glowly.glowly_api.models.User

fun validateHierarchy(user: User, targetAccount: User) {
    if (targetAccount == user) {
        throw ConflictException("Você não pode editar sua própria conta enquanto logado.")
    }

    /*if (user.role == Role.ADMIN) {
        if (targetAccount.role == Role.ADMIN || targetAccount.role == Role.SYNDIC) {
            throw UnauthorizedException("Você não tem permissão para gerenciar um usuário com cargo igual ou superior ao seu.")
        }
    } else if (user.role == Role.BUSINESS) {
        if (targetAccount.role == Role.BUSINESS) {
            throw UnauthorizedException("Você não tem permissão para gerenciar um usuário com cargo igual ou superior ao seu.")
        }
    }*/
}