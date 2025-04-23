package com.tech_dep.project_flow.service

import com.tech_dep.project_flow.dto.*
import com.tech_dep.project_flow.entity.*
import com.tech_dep.project_flow.enums.UserRole
import com.tech_dep.project_flow.exception.ProjectNotFoundException
import com.tech_dep.project_flow.exception.ProjectUserNotFoundException
import com.tech_dep.project_flow.exception.UserAlreadyInProjectException
import com.tech_dep.project_flow.exception.UserNotFoundException
import com.tech_dep.project_flow.repository.ProjectRepository
import com.tech_dep.project_flow.repository.ProjectUserRepository
import com.tech_dep.project_flow.repository.UserRepository
import com.tech_dep.project_flow.utils.JwtUtils
import com.tech_dep.project_flow.utils.ProjectUserUtils
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val projectUserRepository: ProjectUserRepository,
    private val userRepository: UserRepository,
    private val jwtUtils: JwtUtils,
    private val projectUserUtils: ProjectUserUtils
) {
    private val log = KotlinLogging.logger {}

    @Transactional
    fun createProject(accessToken: String, createProjectRequest: CreateProjectRequestDto): ProjectDto {
        log.info { "Создание нового проекта: ${createProjectRequest.title}" }

        val clientId = jwtUtils.extractId(accessToken)
        val user = userRepository.findByUuid(clientId)
        if (user == null) {
            log.error { "Пользователь $clientId не найден" }
            throw UserNotFoundException()
        }

        val project = Project(
            title = createProjectRequest.title,
            description = createProjectRequest.description,
            key = createProjectRequest.key.ifEmpty {
                createProjectRequest.title.replace(Regex("\\s"), "").take(10).uppercase()
            },
        )

        val projectUser = ProjectUser(
            user = user,
            role = UserRole.ADMIN
        )
        project.addUser(projectUser)

        val savedProject = projectRepository.save(project)

        log.info { "Проект ${savedProject.title} успешно создан" }

        return savedProject.toDto()
    }

    fun getAllProjects(accessToken: String): List<ProjectDto> {
        log.info { "Получение всех проектов клиента" }

        val clientId = jwtUtils.extractId(accessToken)
        if (userRepository.findByUuid(clientId) == null) {
            log.error { "Пользователь $clientId не найден" }
            throw UserNotFoundException()
        }

        return projectUserRepository.findProjectsByUserUuid(clientId).map { it.toDto() }
    }

    fun getProject(accessToken: String, projectId: UUID): ProjectDto {
        log.info { "Получение проекта $projectId" }

        projectUserUtils.checkParticipation(accessToken, projectId)

        val project = projectRepository.findByUuid(projectId)

        if (project == null) {
            log.error { "Проект $projectId не найден" }
            throw ProjectNotFoundException(projectId)
        }

        return project.toDto()
    }

    fun updateProject(accessToken: String, projectId: UUID, updateProjectRequest: UpdateProjectRequestDto): ProjectDto {
        log.info { "Обновление проекта $projectId" }

        val project = projectRepository.findByUuid(projectId)

        if (project == null) {
            log.error { "Проект $projectId не найден" }
            throw ProjectNotFoundException(projectId)
        }

        projectUserUtils.checkAdminAccess(accessToken, projectId)

        project.title = updateProjectRequest.title
        project.description = updateProjectRequest.description
        project.key = updateProjectRequest.key
        val updatedProject = projectRepository.save(project)
        log.info { "Проект $projectId успешно обновлен" }

        return updatedProject.toDto()
    }

    @Transactional
    fun deleteProject(accessToken: String, projectId: UUID) {
        log.info { "Удаление проекта $projectId" }

        val project = projectRepository.findByUuid(projectId)
        if (project == null) {
            log.error { "Проект $projectId не найден" }
            throw ProjectNotFoundException(projectId)
        }

        projectUserUtils.checkAdminAccess(accessToken, projectId)

        projectRepository.deleteByUuid(projectId)
        log.info { "Проект $projectId успешно удален" }
    }

    @Transactional
    fun addUser(accessToken: String, projectId: UUID, userData: AddUserRequestDto): MessageResponseDto {
        log.info { "Добавление пользователя ${userData.email} в проект $projectId" }

        projectUserUtils.checkAdminAccess(accessToken, projectId)

        val project = projectRepository.findByUuid(projectId)
        if (project == null) {
            log.error { "Проект $projectId не найден" }
            throw ProjectNotFoundException(projectId)
        }

        val user = userRepository.findByEmail(userData.email.lowercase())
        if (user == null) {
            log.error { "Пользователь ${userData.email} не найден" }
            throw UserNotFoundException()
        }

        if (projectUserRepository.findByProjectUuidAndUserUuid(projectId, user.uuid) != null) {
            log.error { "Пользователь ${userData.email} уже в проекте $projectId" }
            throw UserAlreadyInProjectException()
        }

        val projectUser = ProjectUser(
            user = user,
            role = userData.role
        )
        project.addUser(projectUser)

        log.info { "Пользователь добавлен в проект" }

        return MessageResponseDto("User was successfully added", true)
    }

    fun getUsers(accessToken: String, projectId: UUID, page: Int, size: Int): List<UserDto> {
        log.info { "Получение пользователей проекта $projectId" }

        projectUserUtils.checkParticipation(accessToken, projectId)

        val pageable = PageRequest.of(page - 1, size)
        val projectUsers = projectUserRepository.findAllByProjectUuid(projectId, pageable)

        return projectUsers.content.map { it.user!!.toDto() }
    }

    fun getUsersWithRoles(accessToken: String, projectId: UUID, page: Int, size: Int): List<UserWithRoleDto> {
        log.info { "Получение пользователей с ролями проекта $projectId" }

        projectUserUtils.checkParticipation(accessToken, projectId)

        val pageable = PageRequest.of(page - 1, size)
        val projectUsers = projectUserRepository.findAllByProjectUuid(projectId, pageable)

        return projectUsers.content.map {
            val user = it.user!!
            UserWithRoleDto(id = user.uuid, name = user.name, email = user.email, role = it.role)
        }
    }

    fun changeUserRole(
        accessToken: String,
        projectId: UUID,
        userId: UUID,
        roleData: ChangeUserRoleRequestDto
    ): MessageResponseDto {
        log.info { "Смена роли пользователя $userId в проекте $projectId" }

        projectUserUtils.checkAdminAccess(accessToken, projectId)

        val projectUser = projectUserRepository.findByProjectUuidAndUserUuid(projectId, userId)

        if (projectUser == null) {
            log.error { "Нет записи о пользователе $userId в проекте $projectId!" }
            throw ProjectUserNotFoundException(projectId, userId)
        }

        projectUser.role = roleData.role
        projectUserRepository.save(projectUser)
        log.info { "Смена роли завершена успешно" }

        return MessageResponseDto("Role successfully changed", true)
    }

    @Transactional
    fun excludeUser(accessToken: String, projectId: UUID, userId: UUID) {
        log.info { "Исключение пользователя $userId из проекта $projectId" }

        val project = projectRepository.findByUuid(projectId)
        if (project == null) {
            log.error { "Проект $projectId не найден" }
            throw ProjectNotFoundException(projectId)
        }

        if (userRepository.findByUuid(userId) == null) {
            log.error { "Пользователь $userId не найден" }
            throw UserNotFoundException()
        }

        projectUserUtils.checkAdminAccess(accessToken, projectId)

        val projectUser = project.users.find { it.user?.uuid == userId }
        if (projectUser == null) {
            log.error { "Пользователь $userId не является участником проекта $projectId" }
            throw ProjectUserNotFoundException(projectId, userId)
        }

        project.removeUser(projectUser)

        log.info { "Пользователь исключен" }
    }
}