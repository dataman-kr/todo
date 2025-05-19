package com.example.todo.domain.model

/**
 * User domain entity representing a registered user.
 * This is a core domain entity and contains business logic.
 */
class User private constructor(
    val id: UserId?,
    private var _email: String,
    private var _password: String,
    private var _nickname: String,
    private var _failedLoginAttempts: Int = 0,
    private var _accountLocked: Boolean = false
) {
    // Value object for User ID
    data class UserId(val value: Long)

    // Properties with getters
    val email: String
        get() = _email

    val password: String
        get() = _password

    val nickname: String
        get() = _nickname

    val failedLoginAttempts: Int
        get() = _failedLoginAttempts

    val accountLocked: Boolean
        get() = _accountLocked

    // Business methods
    fun updateNickname(newNickname: String) {
        require(newNickname.isNotBlank()) { "Nickname cannot be blank" }
        _nickname = newNickname
    }

    fun updatePassword(newPassword: String) {
        require(newPassword.isNotBlank()) { "Password cannot be blank" }
        _password = newPassword
    }

    fun recordLoginFailure() {
        _failedLoginAttempts++
        if (_failedLoginAttempts >= 5) {
            _accountLocked = true
        }
    }

    fun resetLoginAttempts() {
        _failedLoginAttempts = 0
    }

    fun unlockAccount() {
        _accountLocked = false
        _failedLoginAttempts = 0
    }

    // Factory methods
    companion object {
        fun create(email: String, password: String, nickname: String): User {
            require(email.isNotBlank()) { "Email cannot be blank" }
            require(password.isNotBlank()) { "Password cannot be blank" }
            require(nickname.isNotBlank()) { "Nickname cannot be blank" }

            return User(
                id = null,
                _email = email,
                _password = password,
                _nickname = nickname
            )
        }

        fun reconstitute(
            id: Long, 
            email: String, 
            password: String, 
            nickname: String, 
            failedLoginAttempts: Int = 0, 
            accountLocked: Boolean = false
        ): User {
            require(id > 0) { "ID must be positive" }
            require(email.isNotBlank()) { "Email cannot be blank" }
            require(password.isNotBlank()) { "Password cannot be blank" }
            require(nickname.isNotBlank()) { "Nickname cannot be blank" }
            require(failedLoginAttempts >= 0) { "Failed login attempts cannot be negative" }

            return User(
                id = UserId(id),
                _email = email,
                _password = password,
                _nickname = nickname,
                _failedLoginAttempts = failedLoginAttempts,
                _accountLocked = accountLocked
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "User(id=$id, email='$_email', nickname='$_nickname')"
    }
}
