package com.example.todo.infrastructure.persistence

import com.example.todo.application.port.out.UserPersistencePort
import com.example.todo.domain.model.User
import org.springframework.stereotype.Component

/**
 * Adapter for UserPersistencePort.
 * This implements the application's output port and adapts it to the JPA repository.
 */
@Component
class UserPersistenceAdapter(private val userJpaRepository: UserJpaRepository) : UserPersistencePort {

    override fun findAll(): List<User> {
        return userJpaRepository.findAll().map { mapToDomainEntity(it) }
    }

    override fun findById(id: Long): User? {
        return userJpaRepository.findById(id).map { mapToDomainEntity(it) }.orElse(null)
    }

    override fun findByEmail(email: String): User? {
        return userJpaRepository.findByEmail(email)?.let { mapToDomainEntity(it) }
    }

    override fun save(user: User): User {
        val userJpaEntity = mapToJpaEntity(user)
        val savedEntity = userJpaRepository.save(userJpaEntity)
        return mapToDomainEntity(savedEntity)
    }

    override fun deleteById(id: Long) {
        userJpaRepository.deleteById(id)
    }

    override fun existsById(id: Long): Boolean {
        return userJpaRepository.existsById(id)
    }

    override fun existsByEmail(email: String): Boolean {
        return userJpaRepository.existsByEmail(email)
    }

    private fun mapToDomainEntity(jpaEntity: UserJpaEntity): User {
        return User.reconstitute(
            id = jpaEntity.id,
            email = jpaEntity.email,
            password = jpaEntity.password,
            nickname = jpaEntity.nickname,
            failedLoginAttempts = jpaEntity.failedLoginAttempts,
            accountLocked = jpaEntity.accountLocked
        )
    }

    private fun mapToJpaEntity(domainEntity: User): UserJpaEntity {
        return UserJpaEntity(
            id = domainEntity.id?.value ?: 0,
            email = domainEntity.email,
            password = domainEntity.password,
            nickname = domainEntity.nickname,
            failedLoginAttempts = domainEntity.failedLoginAttempts,
            accountLocked = domainEntity.accountLocked
        )
    }
}
