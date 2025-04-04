package com.tech_dep.project_flow.service

import com.tech_dep.project_flow.dto.*
import com.tech_dep.project_flow.entity.Task
import com.tech_dep.project_flow.entity.toDto
import com.tech_dep.project_flow.enums.TaskStatus
import com.tech_dep.project_flow.exception.AccessDeniedException
import com.tech_dep.project_flow.exception.ProjectNotFoundException
import com.tech_dep.project_flow.exception.TaskNotFoundException
import com.tech_dep.project_flow.repository.ProjectRepository
import com.tech_dep.project_flow.repository.ProjectUserRepository
import com.tech_dep.project_flow.repository.TaskRepository
import com.tech_dep.project_flow.utils.JwtUtils
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class TaskService(
    val taskRepository: TaskRepository,
    val projectRepository: ProjectRepository,
    private val projectUserRepository: ProjectUserRepository,
    private val jwtUtils: JwtUtils,
) {
    val log = KotlinLogging.logger {}
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")

    fun getTaskById(accessToken: String, taskId: UUID): TaskDto {
        log.info { "Получение задачи с ID: $taskId" }

        val task = taskRepository.findByUuid(taskId)
        if (task == null) {
            log.error { "Задача с ID $taskId не найдена!" }
            throw TaskNotFoundException(taskId)
        }

        val clientId = jwtUtils.extractId(accessToken)
        if (projectUserRepository.findByProjectUuidAndUserUuid(task.project!!.uuid, clientId) == null) {
            log.error { "Пользователь $clientId не имеет прав для просмотра задач проекта ${task.project!!.uuid}" }
            throw AccessDeniedException("User does not have access to see project's tasks")
        }

        log.info { "Задача с ID: $taskId найдена" }

        return task.toDto()
    }

    fun getTasksByProjectId(
        accessToken: String,
        projectId: UUID,
        page: Int,
        size: Int,
        sortOrder: Sort.Direction?,
        sortBy: String?
    ): List<TaskDto> {
        log.info { "Получение задач проекта с ID: $projectId" }

        val clientId = jwtUtils.extractId(accessToken)
        if (projectUserRepository.findByProjectUuidAndUserUuid(projectId, clientId) == null) {
            log.error { "Пользователь $clientId не имеет прав для просмотра задач проекта $projectId" }
            throw AccessDeniedException("User does not have access to see project's tasks")
        }

        val pageable: Pageable = if (sortOrder != null && sortBy != null) {
            PageRequest.of(page - 1, size, Sort.by(sortOrder, sortBy))
        } else {
            PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "id"))
        }

        val tasks = taskRepository.findAllByProjectUuid(projectId, pageable)

        return tasks.content.map { it.toDto() }
    }

    fun addTask(accessToken: String, taskDto: CreateTaskRequestDto): TaskDto {
        log.info { "Добавление задачи '${taskDto.title}'" }
        val project = projectRepository.findByUuid(taskDto.projectId)

        if (project == null) {
            log.error { "Проект с ID ${taskDto.projectId} не найден!" }
            throw ProjectNotFoundException(taskDto.projectId)
        }

        val clientId = jwtUtils.extractId(accessToken)
        if (projectUserRepository.findByProjectUuidAndUserUuid(taskDto.projectId, clientId) == null) {
            log.error { "Пользователь $clientId не имеет прав для добавления задач в проект ${taskDto.projectId}" }
            throw AccessDeniedException("User does not have access to add tasks in project")
        }

        val keyNumber = taskRepository.countByProjectId(project.id!!).toInt() + 1

        val newTask = Task(
            project = project,
            title = taskDto.title,
            description = taskDto.description,
            key = "${project.key}-${keyNumber}",
            type = taskDto.type,
            status = TaskStatus.OPEN,
            priority = taskDto.priority,
            authorId = taskDto.authorId,
            executorId = taskDto.executorId,
            createdDate = LocalDateTime.now().format(formatter),
        )

        val savedTask = taskRepository.save(newTask)
        log.info { "Задача '${taskDto.title}' добавлена" }

        return savedTask.toDto()
    }

    fun updateTask(accessToken: String, taskId: UUID, taskDto: UpdateTaskRequestDto): TaskDto {
        log.info { "Обновление задачи c ID: $taskId" }
        val task = taskRepository.findByUuid(taskId)

        if (task == null) {
            log.error { "Задача с ID: $taskId не найдена для обновления" }
            throw TaskNotFoundException(taskId)
        }

        val clientId = jwtUtils.extractId(accessToken)
        if (projectUserRepository.findByProjectUuidAndUserUuid(task.project!!.uuid, clientId) == null) {
            log.error { "Пользователь $clientId не имеет прав для обновления задач в проекте ${task.project!!.uuid}" }
            throw AccessDeniedException("User does not have access to update tasks in project")
        }

        task.title = taskDto.title ?: task.title
        task.description = taskDto.description ?: task.description
        task.type = taskDto.type ?: task.type
        task.status = taskDto.status ?: task.status
        task.priority = taskDto.priority ?: task.priority
        task.executorId = taskDto.executorId ?: task.executorId
        task.updatedDate = LocalDateTime.now().format(formatter)
        val updatedTask = taskRepository.save(task)
        log.info { "Задача c ID: $taskId обновлена" }

        return updatedTask.toDto()
    }
}