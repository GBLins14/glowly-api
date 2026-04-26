package com.glowly.identity.utils

import com.glowly.identity.exceptions.ConflictException

fun checkDuplicate(value: Any?, message: String) {
    if (value != null) throw ConflictException(message)
}