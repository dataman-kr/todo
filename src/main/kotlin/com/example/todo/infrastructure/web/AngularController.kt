package com.example.todo.infrastructure.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

/**
 * Controller for serving the Angular application.
 * This controller forwards all non-API requests to the Angular app.
 */
@Controller
class AngularController {

    /**
     * Forward all requests to the Angular app.
     * This is necessary for Angular's client-side routing to work.
     */
    @GetMapping(value = [
        "/login",
        "/signup",
        "/todos",
        "/todos/**",
        "/admin/**"
    ])
    fun forwardToAngular(): String {
        return "forward:/index.html"
    }
}
