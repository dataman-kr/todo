package com.example.todo.infrastructure.web

import com.example.todo.application.port.`in`.UserAuthenticationUseCase
import com.example.todo.application.port.`in`.UserRegistrationUseCase
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

/**
 * Controller for user registration and authentication.
 */
@Controller
class UserController(
    private val userRegistrationUseCase: UserRegistrationUseCase,
    private val userAuthenticationUseCase: UserAuthenticationUseCase
) {

    /**
     * Display the signup form.
     */
    @GetMapping("/signup")
    fun showSignupForm(model: Model): String {
        model.addAttribute("user", UserFormData())
        return "signup"
    }

    /**
     * Handle the submission of the signup form.
     */
    @PostMapping("/signup")
    fun registerUser(
        @ModelAttribute("user") userForm: UserFormData,
        redirectAttributes: RedirectAttributes
    ): String {
        try {
            val command = UserRegistrationUseCase.RegisterUserCommand(
                email = userForm.email,
                password = userForm.password,
                nickname = userForm.nickname
            )
            userRegistrationUseCase.registerUser(command)
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please login.")
            return "redirect:/login"
        } catch (e: IllegalStateException) {
            redirectAttributes.addFlashAttribute("errorMessage", e.message)
            redirectAttributes.addFlashAttribute("user", userForm)
            return "redirect:/signup"
        }
    }

    /**
     * Display the login form.
     */
    @GetMapping("/login")
    fun showLoginForm(model: Model): String {
        model.addAttribute("user", LoginFormData())
        return "login"
    }

    /**
     * Handle the submission of the login form.
     */
    @PostMapping("/login")
    fun loginUser(
        @ModelAttribute("user") loginForm: LoginFormData,
        redirectAttributes: RedirectAttributes,
        response: HttpServletResponse,
        session: HttpSession
    ): String {
        val query = UserAuthenticationUseCase.AuthenticateUserQuery(
            email = loginForm.email,
            password = loginForm.password
        )
        val user = userAuthenticationUseCase.authenticateUser(query)

        return if (user != null && !user.accountLocked) {
            // If authentication was successful and account is not locked, add JWT token to response
            if (user.token != null) {
                // Add token to response header
                response.addHeader("Authorization", "Bearer ${user.token}")

                // Also add token as a cookie for frontend access
                val cookie = Cookie("jwt_token", user.token)
                cookie.path = "/"
                cookie.maxAge = 86400 // 1 day
                cookie.isHttpOnly = true
                response.addCookie(cookie)

                // Set user information in session
                session.setAttribute("userId", user.id)
                session.setAttribute("userNickname", user.nickname)

                "redirect:/todos"
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Authentication error")
                redirectAttributes.addFlashAttribute("user", loginForm)
                "redirect:/login"
            }
        } else if (user != null && user.accountLocked) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your account is locked. Please contact an administrator.")
            redirectAttributes.addFlashAttribute("user", loginForm)
            "redirect:/login"
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid email or password")
            redirectAttributes.addFlashAttribute("user", loginForm)
            "redirect:/login"
        }
    }

    /**
     * Handle logout.
     */
    @GetMapping("/logout")
    fun logout(response: HttpServletResponse): String {
        // Clear the JWT token cookie
        val cookie = Cookie("jwt_token", "")
        cookie.path = "/"
        cookie.maxAge = 0
        cookie.isHttpOnly = true
        response.addCookie(cookie)

        return "redirect:/login"
    }

    /**
     * Data class for the user registration form.
     */
    data class UserFormData(
        val email: String = "",
        val password: String = "",
        val nickname: String = ""
    )

    /**
     * Data class for the login form.
     */
    data class LoginFormData(
        val email: String = "",
        val password: String = ""
    )
}
