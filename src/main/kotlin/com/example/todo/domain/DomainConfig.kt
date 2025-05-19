package com.example.todo.domain

import com.example.todo.application.port.out.TodoPersistencePort
import com.example.todo.domain.service.TodoDomainService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration for domain layer beans.
 */
@Configuration
class DomainConfig {

    @Bean
    fun todoDomainService(todoPersistencePort: TodoPersistencePort): TodoDomainService {
        // Create an adapter on the fly that converts TodoPersistencePort to TodoRepository
        val todoRepositoryAdapter = object : com.example.todo.domain.repository.TodoRepository {
            override fun findAll(): List<com.example.todo.domain.model.Todo> {
                return todoPersistencePort.findAll()
            }

            override fun findById(id: com.example.todo.domain.model.Todo.TodoId): com.example.todo.domain.model.Todo? {
                return todoPersistencePort.findById(id.value)
            }

            override fun save(todo: com.example.todo.domain.model.Todo): com.example.todo.domain.model.Todo {
                return todoPersistencePort.save(todo)
            }

            override fun deleteById(id: com.example.todo.domain.model.Todo.TodoId) {
                todoPersistencePort.deleteById(id.value)
            }

            override fun existsById(id: com.example.todo.domain.model.Todo.TodoId): Boolean {
                return todoPersistencePort.existsById(id.value)
            }
        }

        return TodoDomainService(todoRepositoryAdapter)
    }
}
