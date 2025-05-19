package com.example.todo.infrastructure.web

import com.example.todo.application.port.`in`.AdminUseCase
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
class AdminController(
    private val adminUseCase: AdminUseCase
) {

    @GetMapping("/users")
    fun listUsers(model: Model): String {
        val users = adminUseCase.getAllUsers()
        model.addAttribute("users", users)
        return "admin/users"
    }

    @PostMapping("/users/{userId}/unlock")
    fun unlockUser(
        @PathVariable userId: Long,
        redirectAttributes: RedirectAttributes
    ): String {
        try {
            val command = AdminUseCase.UnlockUserAccountCommand(userId)
            val user = adminUseCase.unlockUserAccount(command)
            redirectAttributes.addFlashAttribute("successMessage", "Account for ${user.email} has been unlocked")
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute("errorMessage", e.message)
        }
        return "redirect:/admin/users"
    }
}