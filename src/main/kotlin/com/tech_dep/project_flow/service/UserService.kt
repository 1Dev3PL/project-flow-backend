package com.tech_dep.project_flow.service

import com.tech_dep.project_flow.dto.UserDto
import com.tech_dep.project_flow.entity.toDto
import com.tech_dep.project_flow.exception.UserNotFoundException
import com.tech_dep.project_flow.repository.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(val userRepository: UserRepository) {
    private val log = KotlinLogging.logger {}

    fun getUser(userId: UUID): UserDto {
        val user = userRepository.findByUuid(userId)?.toDto()

        if (user == null) {
            log.error { "Пользователь с ID: $userId не найден" }
            throw UserNotFoundException()
        }

        return user
    }
}