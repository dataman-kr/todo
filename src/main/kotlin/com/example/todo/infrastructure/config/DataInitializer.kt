package com.example.todo.infrastructure.config

import com.example.todo.application.port.out.UserPersistencePort
import com.example.todo.domain.model.User
import com.example.todo.infrastructure.security.Sha256PasswordEncoder
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.Transactional

/**
 * Configuration class for initializing data on application startup.
 */
@Configuration
class DataInitializerConfig {

    @Bean
    fun dataInitializer(
        userPersistencePort: UserPersistencePort,
        passwordEncoder: Sha256PasswordEncoder
    ): CommandLineRunner {
        return CommandLineRunner {
            initializeAdminUser(userPersistencePort, passwordEncoder)
        }
    }

    @Transactional
    fun initializeAdminUser(
        userPersistencePort: UserPersistencePort,
        passwordEncoder: Sha256PasswordEncoder
    ) {
        // Check if admin user already exists
        if (!userPersistencePort.existsByEmail("admin")) {
            println("Creating admin user...")
            
            // Create admin user
            val adminUser = User.create(
                email = "admin@intelliantech.com",
                password = passwordEncoder.encode("admin"),
                nickname = "Administrator"
            )
            
            // Save admin user
            userPersistencePort.save(adminUser)
            
            println("Admin user created successfully")
        } else {
            println("Admin user already exists")
        }
    }
}