package com.example.todo.application.port.`in`

import com.example.todo.domain.model.Todo
import com.example.todo.domain.model.User

/**
 * Input port for querying todos.
 * This is a use case interface that will be implemented by an application service.
 */
interface GetTodoQuery {
    fun getAllTodos(): List<TodoDto>
    fun getTodosByUser(userId: Long): List<TodoDto>
    fun getTodoById(id: Long): TodoDto

    /**
     * DTO for Todo entity to be used in the application layer.
     */
    data class TodoDto(
        val id: Long,
        val title: String,
        val description: String?,
        val isDone: Boolean
    ) {
        companion object {
            fun fromDomain(todo: Todo): TodoDto {
                return TodoDto(
                    id = todo.id?.value ?: 0,
                    title = todo.title,
                    description = todo.description,
                    isDone = todo.isDone
                )
            }
        }
    }
}
