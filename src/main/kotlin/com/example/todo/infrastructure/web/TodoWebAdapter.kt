package com.example.todo.infrastructure.web

import com.example.todo.api.TodoApi
import com.example.todo.application.port.`in`.GetTodoQuery
import com.example.todo.application.port.`in`.ManageTodoCommand
import com.example.todo.model.TodoRequest
import com.example.todo.model.TodoResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

/**
 * Web adapter for Todo API.
 * This implements the generated TodoApi interface and delegates to the application services.
 */
@RestController
class TodoWebAdapter(
    private val getTodoQuery: GetTodoQuery,
    private val manageTodoCommand: ManageTodoCommand
) : TodoApi {

    override fun getAllTodos(): ResponseEntity<List<TodoResponse>> {
        val todos = getTodoQuery.getAllTodos()
        val response = todos.map { mapToApiResponse(it) }
        return ResponseEntity.ok(response)
    }

    override fun getTodoById(id: Long): ResponseEntity<TodoResponse> {
        return try {
            val todo = getTodoQuery.getTodoById(id)
            ResponseEntity.ok(mapToApiResponse(todo))
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    override fun createTodo(todoRequest: TodoRequest): ResponseEntity<TodoResponse> {
        val command = ManageTodoCommand.CreateTodoCommand(
            title = todoRequest.title,
            description = todoRequest.description,
            isDone = todoRequest.isDone ?: false
        )
        
        val createdTodo = manageTodoCommand.createTodo(command)
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToApiResponse(createdTodo))
    }

    override fun updateTodo(id: Long, todoRequest: TodoRequest): ResponseEntity<TodoResponse> {
        return try {
            val command = ManageTodoCommand.UpdateTodoCommand(
                title = todoRequest.title,
                description = todoRequest.description,
                isDone = todoRequest.isDone ?: false
            )
            
            val updatedTodo = manageTodoCommand.updateTodo(id, command)
            ResponseEntity.ok(mapToApiResponse(updatedTodo))
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    override fun deleteTodo(id: Long): ResponseEntity<Unit> {
        return try {
            manageTodoCommand.deleteTodo(id)
            ResponseEntity.noContent().build()
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    private fun mapToApiResponse(dto: GetTodoQuery.TodoDto): TodoResponse {
        return TodoResponse(
            id = dto.id,
            title = dto.title,
            description = dto.description,
            isDone = dto.isDone
        )
    }

    private fun mapToApiResponse(dto: ManageTodoCommand.TodoDto): TodoResponse {
        return TodoResponse(
            id = dto.id,
            title = dto.title,
            description = dto.description,
            isDone = dto.isDone
        )
    }
}