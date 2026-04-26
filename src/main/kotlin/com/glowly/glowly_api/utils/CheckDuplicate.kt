package com.glowly.glowly_api.utils

import com.glowly.glowly_api.exceptions.ConflictException

fun checkDuplicate(value: Any?, message: String) {
    if (value != null) throw ConflictException(message)
}