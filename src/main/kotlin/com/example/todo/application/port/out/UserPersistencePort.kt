package com.example.todo.application.port.out

import com.example.todo.domain.model.User

/**
 * Output port for user persistence.
 * This is a port that will be implemented by an adapter in the infrastructure layer.
 */
interface UserPersistencePort {
    fun findAll(): List<User>
    fun findById(id: Long): User?
    fun findByEmail(email: String): User?
    fun save(user: User): User
    fun deleteById(id: Long)
    fun existsById(id: Long): Boolean
    fun existsByEmail(email: String): Boolean
}