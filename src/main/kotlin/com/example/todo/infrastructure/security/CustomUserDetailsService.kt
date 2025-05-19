package com.example.todo.infrastructure.security

import com.example.todo.application.port.out.UserPersistencePort
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(private val userPersistencePort: UserPersistencePort) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userPersistencePort.findByEmail(username)
            ?: throw UsernameNotFoundException("User with email $username not found")

        // Check if account is locked
        val isAccountNonLocked = !user.accountLocked

        val authorities = mutableListOf(SimpleGrantedAuthority("ROLE_USER"))
        
        // For simplicity, we're assuming the first user is an admin
        // In a real application, you would have a proper role management system
        if (user.id?.value == 1L) {
            authorities.add(SimpleGrantedAuthority("ROLE_ADMIN"))
        }

        return User(
            user.email,
            user.password,
            true, // enabled
            true, // accountNonExpired
            true, // credentialsNonExpired
            isAccountNonLocked,
            authorities
        )
    }
}