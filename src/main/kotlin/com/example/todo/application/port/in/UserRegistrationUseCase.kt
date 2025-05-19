package com.example.todo.application.port.`in`

/**
 * Input port for user registration.
 * This is a port that will be used by the web adapter to register users.
 */
interface UserRegistrationUseCase {
    fun registerUser(command: RegisterUserCommand): UserDto

    data class RegisterUserCommand(
        val email: String,
        val password: String,
        val nickname: String
    )

    data class UserDto(
        val id: Long?,
        val email: String,
        val nickname: String,
        val token: String? = null,
        val accountLocked: Boolean = false
    ) {
        companion object {
            fun fromDomain(user: com.example.todo.domain.model.User, token: String? = null): UserDto {
                return UserDto(
                    id = user.id?.value,
                    email = user.email,
                    nickname = user.nickname,
                    token = token,
                    accountLocked = user.accountLocked
                )
            }
        }
    }
}
