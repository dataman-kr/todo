package com.example.todo.domain.repository

import com.example.todo.domain.model.Todo

/**
 * Repository interface for Todo domain entity.
 * This is a port in the hexagonal architecture that will be implemented
 * by an adapter in the infrastructure layer.
 */
interface TodoRepository {
    fun findAll(): List<Todo>
    fun findById(id: Todo.TodoId): Todo?
    fun save(todo: Todo): Todo
    fun deleteById(id: Todo.TodoId)
    fun existsById(id: Todo.TodoId): Boolean
}