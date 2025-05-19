package com.example.todo.infrastructure.web

import com.example.todo.application.port.`in`.AdminUseCase
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

/**
 * Web adapter for Admin API.
 * This provides REST endpoints for admin operations.
 */
@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
class AdminWebAdapter(
    private val adminUseCase: AdminUseCase
) {

    /**
     * Get all users.
     */
    @GetMapping("/users")
    fun getAllUsers(): ResponseEntity<List<AdminUseCase.UserDto>> {
        val users = adminUseCase.getAllUsers()
        return ResponseEntity.ok(users)
    }

    /**
     * Unlock a user account.
     */
    @PostMapping("/users/{userId}/unlock")
    fun unlockUser(@PathVariable userId: Long): ResponseEntity<AdminUseCase.UserDto> {
        val command = AdminUseCase.UnlockUserAccountCommand(userId)
        val user = adminUseCase.unlockUserAccount(command)
        return ResponseEntity.ok(user)
    }
}