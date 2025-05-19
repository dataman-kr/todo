package com.example.todo.infrastructure.persistence

import jakarta.persistence.*

/**
 * JPA entity for Todo.
 * This is an infrastructure concern and should not be used in the domain layer.
 */
@Entity
@Table(name = "todos")
class TodoJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val title: String,

    @Column
    val description: String? = null,

    @Column
    val isDone: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: UserJpaEntity? = null
)
