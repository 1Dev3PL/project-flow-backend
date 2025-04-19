package com.tech_dep.project_flow.service

import com.tech_dep.project_flow.dto.UserDto
import com.tech_dep.project_flow.dto.toDto
import com.tech_dep.project_flow.entity.ProjectUser
import com.tech_dep.project_flow.enums.UserRole
import com.tech_dep.project_flow.exception.ProjectUserNotFoundException
import com.tech_dep.project_flow.exception.UserNotFoundException
import com.tech_dep.project_flow.repository.ProjectUserRepository
import com.tech_dep.project_flow.repository.UserRepository
import com.tech_dep.project_flow.utils.JwtUtils
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val jwtUtils: JwtUtils,
    private val projectUserRepository: ProjectUserRepository
) {
    private val log = KotlinLogging.logger {}

    fun getUser(userId: UUID): UserDto {
        val user = userRepository.findByUuid(userId)?.toDto()

        if (user == null) {
            log.error { "Пользователь с ID: $userId не найден" }
            throw UserNotFoundException()
        }

        return user
    }

    fun getRole(accessToken: String, projectId: UUID): UserRole {
        log.info { "Получение роли пользователя на проекте $projectId" }

        val userId: UUID = jwtUtils.extractId(accessToken)
        val userData: ProjectUser? = projectUserRepository.findByProjectUuidAndUserUuid(projectId, userId)

        if (userData == null) {
            log.error { "Нет записи о пользователе с ID: $userId в проекте с ID: $projectId!" }
            throw ProjectUserNotFoundException(projectId, userId)
        }

        return userData.role
    }
}