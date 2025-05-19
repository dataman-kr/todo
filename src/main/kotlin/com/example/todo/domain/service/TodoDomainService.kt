package com.example.todo.domain.service

import com.example.todo.domain.model.Todo
import com.example.todo.domain.repository.TodoRepository

/**
 * Domain service for Todo entities.
 * Contains business logic that doesn't naturally fit into the Todo entity.
 */
class TodoDomainService(private val todoRepository: TodoRepository) {
    
    /**
     * Completes a todo by its ID.
     * Returns the updated todo or null if not found.
     */
    fun completeTodo(todoId: Todo.TodoId): Todo? {
        val todo = todoRepository.findById(todoId) ?: return null
        todo.markAsDone()
        return todoRepository.save(todo)
    }
    
    /**
     * Uncompletes a todo by its ID.
     * Returns the updated todo or null if not found.
     */
    fun uncompleteTodo(todoId: Todo.TodoId): Todo? {
        val todo = todoRepository.findById(todoId) ?: return null
        todo.markAsUndone()
        return todoRepository.save(todo)
    }
}