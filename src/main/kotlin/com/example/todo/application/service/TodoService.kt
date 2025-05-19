package com.example.todo.application.service

import com.example.todo.application.port.`in`.GetTodoQuery
import com.example.todo.application.port.`in`.ManageTodoCommand
import com.example.todo.application.port.out.TodoPersistencePort
import com.example.todo.application.port.out.UserPersistencePort
import com.example.todo.domain.model.Todo
import com.example.todo.domain.model.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TodoService(
    private val todoPersistencePort: TodoPersistencePort,
    private val userPersistencePort: UserPersistencePort
) : GetTodoQuery, ManageTodoCommand {

    @Transactional(readOnly = true)
    override fun getAllTodos(): List<GetTodoQuery.TodoDto> {
        return todoPersistencePort.findAll().map { GetTodoQuery.TodoDto.fromDomain(it) }
    }

    @Transactional(readOnly = true)
    override fun getTodosByUser(userId: Long): List<GetTodoQuery.TodoDto> {
        val user = userPersistencePort.findById(userId)
            ?: throw NoSuchElementException("User not found with id: $userId")
        return todoPersistencePort.findByUser(user).map { GetTodoQuery.TodoDto.fromDomain(it) }
    }

    @Transactional(readOnly = true)
    override fun getTodoById(id: Long): GetTodoQuery.TodoDto {
        val todo = todoPersistencePort.findById(id)
            ?: throw NoSuchElementException("Todo not found with id: $id")
        return GetTodoQuery.TodoDto.fromDomain(todo)
    }

    @Transactional
    override fun createTodo(command: ManageTodoCommand.CreateTodoCommand): ManageTodoCommand.TodoDto {
        // Find the user if userId is provided
        val user = command.userId?.let { userId ->
            userPersistencePort.findById(userId)
                ?: throw NoSuchElementException("User not found with id: $userId")
        }

        val todo = Todo.create(
            title = command.title,
            description = command.description,
            user = user
        )

        val savedTodo = todoPersistencePort.save(todo)
        return ManageTodoCommand.TodoDto.fromDomain(savedTodo)
    }

    @Transactional
    override fun updateTodo(id: Long, command: ManageTodoCommand.UpdateTodoCommand): ManageTodoCommand.TodoDto {
        val existingTodo = todoPersistencePort.findById(id)
            ?: throw NoSuchElementException("Todo not found with id: $id")

        existingTodo.updateTitle(command.title)
        existingTodo.updateDescription(command.description)

        if (command.isDone && !existingTodo.isDone) {
            existingTodo.markAsDone()
        } else if (!command.isDone && existingTodo.isDone) {
            existingTodo.markAsUndone()
        }

        val savedTodo = todoPersistencePort.save(existingTodo)
        return ManageTodoCommand.TodoDto.fromDomain(savedTodo)
    }

    @Transactional
    override fun deleteTodo(id: Long) {
        if (!todoPersistencePort.existsById(id)) {
            throw NoSuchElementException("Todo not found with id: $id")
        }
        todoPersistencePort.deleteById(id)
    }

    @Transactional
    override fun completeTodo(id: Long): ManageTodoCommand.TodoDto {
        val todo = todoPersistencePort.findById(id)
            ?: throw NoSuchElementException("Todo not found with id: $id")

        todo.markAsDone()
        val savedTodo = todoPersistencePort.save(todo)
        return ManageTodoCommand.TodoDto.fromDomain(savedTodo)
    }

    @Transactional
    override fun uncompleteTodo(id: Long): ManageTodoCommand.TodoDto {
        val todo = todoPersistencePort.findById(id)
            ?: throw NoSuchElementException("Todo not found with id: $id")

        todo.markAsUndone()
        val savedTodo = todoPersistencePort.save(todo)
        return ManageTodoCommand.TodoDto.fromDomain(savedTodo)
    }
}
