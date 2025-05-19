package com.example.todo.infrastructure.web

import com.example.todo.application.port.`in`.GetTodoQuery
import com.example.todo.application.port.`in`.ManageTodoCommand
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

/**
 * Controller for Todo web views using Thymeleaf.
 * This controller handles the web views for the Todo application.
 */
@Controller
@RequestMapping("/todos")
class TodoViewController(
    private val getTodoQuery: GetTodoQuery,
    private val manageTodoCommand: ManageTodoCommand
) {

    /**
     * Check if user is authenticated and redirect to login if not
     * Returns the user ID if authenticated, null otherwise
     */
    private fun checkAuthentication(session: HttpSession): Long? {
        val userId = session.getAttribute("userId") as? Long
        return userId
    }

    /**
     * Display the list of todos.
     */
    @GetMapping
    fun listTodos(model: Model, session: HttpSession): String {
        val userId = checkAuthentication(session) ?: return "redirect:/login"

        // Get todos for the logged-in user
        val todos = getTodoQuery.getTodosByUser(userId)

        // Add user nickname to the model
        val userNickname = session.getAttribute("userNickname") as? String
        model.addAttribute("userNickname", userNickname)
        model.addAttribute("todos", todos)

        return "list"
    }

    /**
     * Display the form for creating a new todo.
     */
    @GetMapping("/create")
    fun showCreateForm(model: Model, session: HttpSession): String {
        val userId = checkAuthentication(session) ?: return "redirect:/login"

        model.addAttribute("todo", TodoFormData())
        return "create"
    }

    /**
     * Handle the submission of the create todo form.
     */
    @PostMapping
    fun createTodo(@ModelAttribute("todo") todoForm: TodoFormData, session: HttpSession): String {
        val userId = checkAuthentication(session) ?: return "redirect:/login"

        val command = ManageTodoCommand.CreateTodoCommand(
            title = todoForm.title,
            description = todoForm.description,
            isDone = false,
            userId = userId
        )
        manageTodoCommand.createTodo(command)
        return "redirect:/todos"
    }

    /**
     * Display the form for editing a todo.
     */
    @GetMapping("/{id}/edit")
    fun showEditForm(@PathVariable id: Long, model: Model, session: HttpSession): String {
        val userId = checkAuthentication(session) ?: return "redirect:/login"

        val todo = getTodoQuery.getTodoById(id)
        model.addAttribute("todo", TodoFormData(
            title = todo.title,
            description = todo.description
        ))
        model.addAttribute("todoId", id)
        return "edit"
    }

    /**
     * Handle the submission of the edit todo form.
     */
    @PostMapping("/{id}/edit")
    fun updateTodo(@PathVariable id: Long, @ModelAttribute("todo") todoForm: TodoFormData, session: HttpSession): String {
        val userId = checkAuthentication(session) ?: return "redirect:/login"

        val todo = getTodoQuery.getTodoById(id)
        val command = ManageTodoCommand.UpdateTodoCommand(
            title = todoForm.title,
            description = todoForm.description,
            isDone = todo.isDone
        )
        manageTodoCommand.updateTodo(id, command)
        return "redirect:/todos"
    }

    /**
     * Handle the deletion of a todo.
     */
    @PostMapping("/{id}/delete")
    fun deleteTodo(@PathVariable id: Long, session: HttpSession): String {
        val userId = checkAuthentication(session) ?: return "redirect:/login"

        manageTodoCommand.deleteTodo(id)
        return "redirect:/todos"
    }

    /**
     * Handle the toggle of todo completion status.
     */
    @PostMapping("/{id}/toggle")
    fun toggleTodoStatus(@PathVariable id: Long, session: HttpSession): String {
        val userId = checkAuthentication(session) ?: return "redirect:/login"

        val todo = getTodoQuery.getTodoById(id)
        if (todo.isDone) {
            manageTodoCommand.uncompleteTodo(id)
        } else {
            manageTodoCommand.completeTodo(id)
        }
        return "redirect:/todos"
    }

    /**
     * Data class for the todo form.
     */
    data class TodoFormData(
        val title: String = "",
        val description: String? = null
    )
}
