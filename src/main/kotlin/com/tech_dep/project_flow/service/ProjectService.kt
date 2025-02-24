package com.tech_dep.project_flow.service

import com.tech_dep.project_flow.dto.*
import com.tech_dep.project_flow.entity.Project
import com.tech_dep.project_flow.entity.ProjectUser
import com.tech_dep.project_flow.entity.toDto
import com.tech_dep.project_flow.enums.UserRole
import com.tech_dep.project_flow.exception.ProjectNotFoundException
import com.tech_dep.project_flow.exception.ProjectUserNotFoundException
import com.tech_dep.project_flow.exception.UserAlreadyInProjectException
import com.tech_dep.project_flow.exception.UserNotFoundException
import com.tech_dep.project_flow.repository.ProjectRepository
import com.tech_dep.project_flow.repository.ProjectUserRepository
import com.tech_dep.project_flow.repository.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val projectUserRepository: ProjectUserRepository,
    private val userRepository: UserRepository,
) {
    private val log = KotlinLogging.logger {}

    @Transactional
    fun createProject(userId: UUID, createProjectRequest: CreateProjectRequestDto): ProjectDto {
        log.info { "Создание нового проекта: ${createProjectRequest.title}" }
        val user = userRepository.findByUuid(userId)
        if (user == null) {
            log.error { "Пользователь с ID: $userId не найден" }
            throw UserNotFoundException()
        }
        val project = Project(
            title = createProjectRequest.title,
            description = createProjectRequest.description,
            key = createProjectRequest.key.ifEmpty {
                createProjectRequest.title.replace(Regex("\\s"), "").take(10).uppercase()
            },
        )
        val savedProject: Project = projectRepository.save(project)

        val projectUser = ProjectUser(
            project = project,
            user = user,
            role = UserRole.ADMIN
        )
        projectUserRepository.save(projectUser)
        log.info { "Проект с ID ${savedProject.id} успешно создан" }

        return savedProject.toDto()
    }

    fun getAllProjects(userId: UUID): List<ProjectDto> {
        log.info { "Получение всех проектов пользователя $userId" }
        return projectUserRepository.findProjectsByUserUuid(userId).map { it.toDto() }
    }

    fun getProject(projectId: UUID): ProjectDto {
        log.info { "Получение проекта с ID: $projectId" }
        val project = projectRepository.findByUuid(projectId)

        if (project == null) {
            log.error { "Проект с ID $projectId не найден" }
            throw ProjectNotFoundException(projectId)
        }

        return project.toDto()
    }

    fun updateProject(projectId: UUID, updateProjectRequest: UpdateProjectRequestDto): ProjectDto {
        log.info { "Обновление проекта с ID: $projectId" }
        val project = projectRepository.findByUuid(projectId)

        if (project == null) {
            log.error { "Проект с ID $projectId не найден для обновления" }
            throw ProjectNotFoundException(projectId)
        }

        project.title = updateProjectRequest.title
        project.description = updateProjectRequest.description
        project.key = updateProjectRequest.key
        val updatedProject = projectRepository.save(project)
        log.info { "Проект с ID $projectId успешно обновлен" }

        return updatedProject.toDto()
    }

    @Transactional
    fun deleteProject(projectId: UUID) {
        log.info { "Удаление проекта с ID: $projectId" }
        if (projectRepository.findByUuid(projectId) != null) {
            projectRepository.deleteByUuid(projectId)
            log.info { "Проект с ID $projectId успешно удален" }
        } else {
            log.error { "Проект с ID $projectId не найден для удаления" }
            throw ProjectNotFoundException(projectId)
        }
    }

    fun addUser(projectId: UUID, userData: AddUserRequestDto): MessageResponseDto {
        log.info { "Добавление пользователя с ID: ${userData.userId} в проект с ID: $projectId" }

        val existingProjectUser = projectUserRepository.findByProjectUuidAndUserUuid(projectId, userData.userId)
        if (existingProjectUser != null) {
            log.error { "Пользователь с ID: ${userData.userId} уже на проекте с ID: $projectId" }
            throw UserAlreadyInProjectException()
        }

        val project = projectRepository.findByUuid(projectId)
        if (project == null) {
            log.error { "Проект с ID $projectId не найден" }
            throw ProjectNotFoundException(projectId)
        }

        val user = userRepository.findByUuid(userData.userId)
        if (user == null) {
            log.error { "Пользователь с ID: ${userData.userId} не найден" }
            throw UserNotFoundException()
        }

        val projectUser = ProjectUser(
            project = project,
            user = user,
            role = userData.role
        )

        projectUserRepository.save(projectUser)

        return MessageResponseDto("User was successfully added", true)
    }

    // TODO - Pagination
    fun getUsers(projectId: UUID): List<UserDto> {
        log.info { "Получение пользователей проекта с ID: $projectId" }
        val users = projectUserRepository.findUsersByProjectUuid(projectId)

        return users.map { it.toDto() }
    }

    fun changeUserRole(projectId: UUID, userId: UUID, roleData: ChangeUserRoleRequestDto): MessageResponseDto {
        log.info { "Смена роли пользователя с ID: $userId в проекте с ID: $projectId" }
        val projectUser = projectUserRepository.findByProjectUuidAndUserUuid(projectId, userId)

        if (projectUser == null) {
            log.error { "Нет записи о пользователе с ID: $userId в проекте с ID: $projectId!" }
            throw ProjectUserNotFoundException(projectId, userId)
        }

        projectUser.role = roleData.role
        projectUserRepository.save(projectUser)
        log.info { "Смена роли завершена успешно" }

        return MessageResponseDto("Role successfully changed", true)
    }
}