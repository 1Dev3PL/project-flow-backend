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
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val projectUserRepository: ProjectUserRepository,
    private val userRepository: UserRepository,
) {
    private val log = KotlinLogging.logger {}

    @Transactional
    fun createProject(userId: Long, projectDto: ProjectDto): Project {
        log.info { "Создание нового проекта: ${projectDto.title}" }
        val user = userRepository.findByIdOrNull(userId)
        if (user == null) {
            log.error { "Пользователь с ID: $userId не найден" }
            throw UserNotFoundException()
        }
        val project = Project(
            title = projectDto.title,
            description = projectDto.description,
            key = projectDto.key.ifEmpty { projectDto.title.replace(Regex("\\s"), "").take(10).uppercase() },
        )
        val savedProject: Project = projectRepository.save(project)

        val projectUser = ProjectUser(
            project = project,
            user = user,
            role = UserRole.ADMIN
        )
        projectUserRepository.save(projectUser)
        log.info { "Проект с ID ${savedProject.id} успешно создан" }

        return savedProject
    }

    fun getAllProjects(userId: Long): List<Project> {
        log.info { "Получение всех проектов пользователя $userId" }
        return projectUserRepository.findProjectsByUserId(userId)
    }

    fun getProject(projectId: Long): ProjectDto? {
        log.info { "Получение проекта с ID: $projectId" }
        val project = projectRepository.findByIdOrNull(projectId)

        if (project == null) {
            log.error { "Проект с ID $projectId не найден" }
            throw ProjectNotFoundException(projectId)
        }

        return project.toDto()
    }

    fun updateProject(id: Long, projectDto: ProjectDto): ProjectDto {
        log.info { "Обновление проекта с ID: $id" }
        val project = projectRepository.findByIdOrNull(id)

        if (project == null) {
            log.error { "Проект с ID $id не найден для обновления" }
            throw ProjectNotFoundException(id)
        }

        project.title = projectDto.title
        project.description = projectDto.description
        project.key = projectDto.key
        val updatedProject = projectRepository.save(project)
        log.info { "Проект с ID $id успешно обновлен" }

        return updatedProject.toDto()
    }

    fun deleteProject(id: Long) {
        log.info { "Удаление проекта с ID: $id" }
        if (projectRepository.existsById(id)) {
            projectRepository.deleteById(id)
            log.info { "Проект с ID $id успешно удален" }
        } else {
            log.error { "Проект с ID $id не найден для удаления" }
            throw ProjectNotFoundException(id)
        }
    }

    fun addUser(projectId: Long, userData: AddUserRequestDto): MessageResponseDto {
        log.info { "Добавление пользователя с ID: ${userData.userId} в проект с ID: $projectId" }

        val existingProjectUser = projectUserRepository.findByProjectIdAndUserId(projectId, userData.userId)
        if (existingProjectUser != null) {
            log.error { "Пользователь с ID: ${userData.userId} уже на проекте с ID: $projectId" }
            throw UserAlreadyInProjectException()
        }

        val project = projectRepository.findByIdOrNull(projectId)
        if (project == null) {
            log.error { "Проект с ID $projectId не найден" }
            throw ProjectNotFoundException(projectId)
        }

        val user = userRepository.findByIdOrNull(userData.userId)
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
    fun getUsers(projectId: Long): List<UserDto> {
        log.info { "Получение пользователей проекта с ID: $projectId" }
        val users = projectUserRepository.findUsersByProjectId(projectId)

        return users.map { it.toDto() }
    }

    fun changeUserRole(projectId: Long, userId: Long, roleData: ChangeUserRoleRequestDto): MessageResponseDto {
        log.info { "Смена роли пользователя с ID: $userId в проекте с ID: $projectId" }
        val projectUser = projectUserRepository.findByProjectIdAndUserId(projectId, userId)

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