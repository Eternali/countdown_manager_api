package org.example.countdownmanagerapi

import java.security.SecureRandom

fun generateSalt(): String {
    val bytes = ByteArray(40)
    return SecureRandom().nextBytes(bytes).toString()
}
