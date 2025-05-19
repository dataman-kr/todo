package com.example.todo.application.port.`in`

/**
 * Input port for admin operations.
 * This is a port that will be used by the web adapter to perform admin operations.
 */
interface AdminUseCase {
    fun unlockUserAccount(command: UnlockUserAccountCommand): UserDto
    fun getAllUsers(): List<UserDto>
    
    data class UnlockUserAccountCommand(
        val userId: Long
    )
    
    data class UserDto(
        val id: Long?,
        val email: String,
        val nickname: String,
        val failedLoginAttempts: Int,
        val accountLocked: Boolean
    ) {
        companion object {
            fun fromDomain(user: com.example.todo.domain.model.User): UserDto {
                return UserDto(
                    id = user.id?.value,
                    email = user.email,
                    nickname = user.nickname,
                    failedLoginAttempts = user.failedLoginAttempts,
                    accountLocked = user.accountLocked
                )
            }
        }
    }
}