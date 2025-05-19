package com.example.todo.infrastructure.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig(private val jwtTokenProvider: JwtTokenProvider) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .headers { headers ->
                headers.frameOptions { it.sameOrigin() }  // Allow frames for H2 console
                headers.referrerPolicy { it.policy(org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN) }  // Set Referrer-Policy
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) }
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers("/api/v1/auth/**", "/h2-console/**").permitAll()
                    .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                    // Allow access to Swagger UI
                    .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/openapi.yml", "/api/v1/swagger-ui.html", "/api/v1/swagger-ui/**").permitAll()
                    // Allow access to static resources for Angular
                    .requestMatchers(
                        "/",
                        "/index.html",
                        "/favicon.ico",
                        "/assets/**",
                        "/*.js",
                        "/*.js.map",
                        "/*.css",
                        "/*.css.map",
                        "/*.ttf",
                        "/*.woff",
                        "/*.woff2"
                    ).permitAll()
                    // Secure API endpoints
                    .requestMatchers("/api/v1/**").authenticated()
                    // Allow Angular routes to be accessed without authentication
                    .requestMatchers("/login", "/signup", "/admin/**", "/todos/**").permitAll()
                    .anyRequest().authenticated()
            }
            // We're not using Spring Security's form login processing
            // because we have a custom login controller
            // We only need the redirect to login page functionality
            .exceptionHandling {
                it.authenticationEntryPoint { request, response, _ ->
                    response.sendRedirect("/login")
                }
            }
            .addFilterBefore(
                JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter::class.java
            )

        return http.build()
    }
}
