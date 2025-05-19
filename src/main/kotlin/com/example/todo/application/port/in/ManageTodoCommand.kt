package com.example.todo.application.port.`in`

import com.example.todo.domain.model.Todo

/**
 * Input port for managing todos.
 * This is a use case interface that will be implemented by an application service.
 */
interface ManageTodoCommand {
    fun createTodo(command: CreateTodoCommand): TodoDto
    fun updateTodo(id: Long, command: UpdateTodoCommand): TodoDto
    fun deleteTodo(id: Long)
    fun completeTodo(id: Long): TodoDto
    fun uncompleteTodo(id: Long): TodoDto

    /**
     * Command for creating a new todo.
     */
    data class CreateTodoCommand(
        val title: String,
        val description: String?,
        val isDone: Boolean = false,
        val userId: Long? = null
    )

    /**
     * Command for updating an existing todo.
     */
    data class UpdateTodoCommand(
        val title: String,
        val description: String?,
        val isDone: Boolean
    )

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
