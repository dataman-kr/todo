package com.example.todo.infrastructure.security

import org.springframework.stereotype.Component
import java.security.MessageDigest
import java.util.Base64

/**
 * Simple password encoder that uses SHA-256 for hashing.
 * In a production environment, you would use a more secure algorithm like BCrypt.
 */
@Component("sha256PasswordEncoder")
class Sha256PasswordEncoder {

    fun encode(rawPassword: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val hash = messageDigest.digest(rawPassword.toByteArray())
        return Base64.getEncoder().encodeToString(hash)
    }

    fun matches(rawPassword: String, encodedPassword: String): Boolean {
        val encodedRawPassword = encode(rawPassword)
        return encodedRawPassword == encodedPassword
    }
}
