package com.example.todo.infrastructure.web

import com.example.todo.application.port.`in`.UserAuthenticationUseCase
import com.example.todo.application.port.`in`.UserRegistrationUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Web adapter for User API.
 * This provides REST endpoints for user registration and authentication.
 */
@RestController
@RequestMapping("/api/v1/auth")
class UserWebAdapter(
    private val userRegistrationUseCase: UserRegistrationUseCase,
    private val userAuthenticationUseCase: UserAuthenticationUseCase
) {

    /**
     * Register a new user.
     */
    @PostMapping("/register")
    fun registerUser(@RequestBody request: RegisterRequest): ResponseEntity<Any> {
        return try {
            val command = UserRegistrationUseCase.RegisterUserCommand(
                email = request.email,
                password = request.password,
                nickname = request.nickname
            )
            userRegistrationUseCase.registerUser(command)
            ResponseEntity.status(HttpStatus.CREATED).body(mapOf("message" to "Registration successful"))
        } catch (e: IllegalStateException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    /**
     * Authenticate a user and return a JWT token.
     */
    @PostMapping("/login")
    fun loginUser(@RequestBody request: LoginRequest): ResponseEntity<Any> {
        val query = UserAuthenticationUseCase.AuthenticateUserQuery(
            email = request.email,
            password = request.password
        )
        val user = userAuthenticationUseCase.authenticateUser(query)

        return if (user != null && !user.accountLocked) {
            if (user.token != null) {
                ResponseEntity.ok(mapOf(
                    "token" to user.token,
                    "userId" to user.id,
                    "nickname" to user.nickname,
                    "roles" to user.roles
                ))
            } else {
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(mapOf("error" to "Authentication error"))
            }
        } else if (user != null && user.accountLocked) {
            ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(mapOf("error" to "Your account is locked. Please contact an administrator."))
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(mapOf("error" to "Invalid email or password"))
        }
    }

    /**
     * Data class for the registration request.
     */
    data class RegisterRequest(
        val email: String,
        val password: String,
        val nickname: String
    )

    /**
     * Data class for the login request.
     */
    data class LoginRequest(
        val email: String,
        val password: String
    )
}
