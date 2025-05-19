package com.example.todo.infrastructure.persistence

import com.example.todo.application.port.out.TodoPersistencePort
import com.example.todo.domain.model.Todo
import com.example.todo.domain.model.User
import org.springframework.stereotype.Component

/**
 * Adapter for TodoPersistencePort.
 * This implements the application's output port and adapts it to the JPA repository.
 */
@Component
class TodoPersistenceAdapter(
    private val todoJpaRepository: TodoJpaRepository,
    private val userJpaRepository: UserJpaRepository
) : TodoPersistencePort {

    override fun findAll(): List<Todo> {
        return todoJpaRepository.findAll().map { mapToDomainEntity(it) }
    }

    override fun findByUser(user: User): List<Todo> {
        // Find the user JPA entity
        val userJpaEntity = user.id?.value?.let { userId ->
            userJpaRepository.findById(userId).orElse(null)
        } ?: return emptyList()

        // Find todos by user
        return todoJpaRepository.findByUser(userJpaEntity).map { mapToDomainEntity(it) }
    }

    override fun findById(id: Long): Todo? {
        return todoJpaRepository.findById(id).map { mapToDomainEntity(it) }.orElse(null)
    }

    override fun save(todo: Todo): Todo {
        val todoJpaEntity = mapToJpaEntity(todo)
        val savedEntity = todoJpaRepository.save(todoJpaEntity)
        return mapToDomainEntity(savedEntity)
    }

    override fun deleteById(id: Long) {
        todoJpaRepository.deleteById(id)
    }

    override fun existsById(id: Long): Boolean {
        return todoJpaRepository.existsById(id)
    }

    private fun mapToDomainEntity(jpaEntity: TodoJpaEntity): Todo {
        // Map user if present
        val user = jpaEntity.user?.let { userJpaEntity ->
            User.reconstitute(
                id = userJpaEntity.id,
                email = userJpaEntity.email,
                password = userJpaEntity.password,
                nickname = userJpaEntity.nickname
            )
        }

        return Todo.reconstitute(
            id = jpaEntity.id,
            title = jpaEntity.title,
            description = jpaEntity.description,
            isDone = jpaEntity.isDone,
            user = user
        )
    }

    private fun mapToJpaEntity(domainEntity: Todo): TodoJpaEntity {
        // Find the user JPA entity if the domain entity has a user
        val userJpaEntity = domainEntity.user?.id?.value?.let { userId ->
            userJpaRepository.findById(userId).orElse(null)
        }

        return TodoJpaEntity(
            id = domainEntity.id?.value ?: 0,
            title = domainEntity.title,
            description = domainEntity.description,
            isDone = domainEntity.isDone,
            user = userJpaEntity
        )
    }
}
