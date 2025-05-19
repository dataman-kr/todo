package com.example.todo.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * JPA repository for TodoJpaEntity.
 * This is an infrastructure concern and should not be used in the domain layer.
 */
@Repository
interface TodoJpaRepository : JpaRepository<TodoJpaEntity, Long> {
    fun findByUser(user: UserJpaEntity): List<TodoJpaEntity>
}
