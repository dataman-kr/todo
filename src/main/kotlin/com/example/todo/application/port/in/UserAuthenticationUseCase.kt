package com.example.todo.application.port.`in`

/**
 * Input port for user authentication.
 * This is a port that will be used by the web adapter to authenticate users.
 */
interface UserAuthenticationUseCase {
    fun authenticateUser(query: AuthenticateUserQuery): UserDto?

    data class AuthenticateUserQuery(
        val email: String,
        val password: String
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
