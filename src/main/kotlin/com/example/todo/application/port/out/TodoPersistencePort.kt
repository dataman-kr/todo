package com.example.todo.application.port.out

import com.example.todo.domain.model.Todo
import com.example.todo.domain.model.User

/**
 * Output port for todo persistence.
 * This is a port that will be implemented by an adapter in the infrastructure layer.
 */
interface TodoPersistencePort {
    fun findAll(): List<Todo>
    fun findByUser(user: User): List<Todo>
    fun findById(id: Long): Todo?
    fun save(todo: Todo): Todo
    fun deleteById(id: Long)
    fun existsById(id: Long): Boolean
}
