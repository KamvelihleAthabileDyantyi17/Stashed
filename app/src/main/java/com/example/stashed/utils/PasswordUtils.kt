package com.example.stashed.utils

import org.mindrot.jbcrypt.BCrypt

object PasswordUtils {

    /** Hash a plain-text password using BCrypt with work factor 12. */
    fun hashPassword(plainText: String): String {
        return BCrypt.hashpw(plainText, BCrypt.gensalt(12))
    }

    /** Verify a plain-text password against a stored BCrypt hash. */
    fun verifyPassword(plainText: String, hash: String): Boolean {
        return try {
            BCrypt.checkpw(plainText, hash)
        } catch (e: Exception) {
            false
        }
    }
}
