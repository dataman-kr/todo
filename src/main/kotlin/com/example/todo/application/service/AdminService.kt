package com.example.todo.application.service

import com.example.todo.application.port.`in`.AdminUseCase
import com.example.todo.application.port.out.UserPersistencePort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminService(
    private val userPersistencePort: UserPersistencePort
) : AdminUseCase {

    @Transactional
    override fun unlockUserAccount(command: AdminUseCase.UnlockUserAccountCommand): AdminUseCase.UserDto {
        val user = userPersistencePort.findById(command.userId)
            ?: throw IllegalArgumentException("User with ID ${command.userId} not found")
        
        user.unlockAccount()
        val savedUser = userPersistencePort.save(user)
        
        return AdminUseCase.UserDto.fromDomain(savedUser)
    }

    @Transactional(readOnly = true)
    override fun getAllUsers(): List<AdminUseCase.UserDto> {
        return userPersistencePort.findAll().map { AdminUseCase.UserDto.fromDomain(it) }
    }
}