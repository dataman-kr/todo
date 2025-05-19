package com.example.todo.application.service

import com.example.todo.application.port.`in`.UserAuthenticationUseCase
import com.example.todo.application.port.`in`.UserRegistrationUseCase
import com.example.todo.application.port.out.UserPersistencePort
import com.example.todo.domain.model.User
import com.example.todo.infrastructure.security.JwtTokenProvider
import com.example.todo.infrastructure.security.Sha256PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userPersistencePort: UserPersistencePort,
    private val passwordEncoder: Sha256PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) : UserRegistrationUseCase, UserAuthenticationUseCase {

    @Transactional
    override fun registerUser(command: UserRegistrationUseCase.RegisterUserCommand): UserRegistrationUseCase.UserDto {
        // Check if user with the same email already exists
        if (userPersistencePort.existsByEmail(command.email)) {
            throw IllegalStateException("User with email ${command.email} already exists")
        }

        // Create a new user with encrypted password
        val user = User.create(
            email = command.email,
            password = passwordEncoder.encode(command.password),
            nickname = command.nickname
        )

        // Save the user
        val savedUser = userPersistencePort.save(user)
        return UserRegistrationUseCase.UserDto.fromDomain(savedUser)
    }

    @Transactional
    override fun authenticateUser(query: UserAuthenticationUseCase.AuthenticateUserQuery): UserAuthenticationUseCase.UserDto? {
        // Find user by email
        val user = userPersistencePort.findByEmail(query.email) ?: return null

        // Check if account is locked
        if (user.accountLocked) {
            return UserAuthenticationUseCase.UserDto.fromDomain(user)
        }

        // Check if password matches
        if (!passwordEncoder.matches(query.password, user.password)) {
            // Record login failure and save
            user.recordLoginFailure()
            userPersistencePort.save(user)
            return null
        }

        // Reset login attempts on successful login
        if (user.failedLoginAttempts > 0) {
            user.resetLoginAttempts()
            userPersistencePort.save(user)
        }

        // Generate JWT token
        val roles = if (user.id?.value == 1L) listOf("USER", "ADMIN") else listOf("USER")
        val token = jwtTokenProvider.createToken(user.email, roles)

        return UserAuthenticationUseCase.UserDto.fromDomain(user, token)
    }
}
