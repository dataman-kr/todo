package com.example.todo.infrastructure.persistence

import jakarta.persistence.*

/**
 * JPA entity for User.
 * This is an infrastructure concern and should not be used in the domain layer.
 */
@Entity
@Table(name = "users")
class UserJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val password: String,

    @Column(nullable = false)
    val nickname: String,

    @Column(nullable = false)
    val failedLoginAttempts: Int = 0,

    @Column(nullable = false)
    val accountLocked: Boolean = false
)
