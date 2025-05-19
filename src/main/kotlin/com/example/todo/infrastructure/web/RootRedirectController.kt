package com.example.todo.infrastructure.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class RootRedirectController {
    @GetMapping("/")
    fun redirectToLogin(): String {
        return "redirect:/login"
    }
}
