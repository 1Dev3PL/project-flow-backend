package com.tech_dep.project_flow.service

import com.tech_dep.project_flow.dto.*
import com.tech_dep.project_flow.entity.Task
import com.tech_dep.project_flow.enums.TaskStatus
import com.tech_dep.project_flow.exception.ProjectNotFoundException
import com.tech_dep.project_flow.exception.TaskNotFoundException
import com.tech_dep.project_flow.exception.UserNotFoundException
import com.tech_dep.project_flow.repository.ProjectRepository
import com.tech_dep.project_flow.repository.TaskRepository
import com.tech_dep.project_flow.repository.UserRepository
import com.tech_dep.project_flow.utils.ProjectUserUtils
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class TaskService(
    private val taskRepository: TaskRepository,
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository,
    private val projectUserUtils: ProjectUserUtils,
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

        projectUserUtils.checkParticipation(accessToken, task.project!!.uuid)

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

        if (projectRepository.findByUuid(projectId) == null) {
            log.error { "Проект с ID $projectId не найден!" }
            throw ProjectNotFoundException(projectId)
        }

        projectUserUtils.checkParticipation(accessToken, projectId)

        val pageable: Pageable = if (sortOrder != null && sortBy != null) {
            PageRequest.of(page - 1, size, Sort.by(sortOrder, sortBy))
        } else {
            PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "id"))
        }

        val tasks = taskRepository.findAllByProjectUuid(projectId, pageable)

        return tasks.map { it.toDto() }
    }

    fun getTasksForDashboard(
        accessToken: String,
        projectId: UUID
    ): DashboardTasksResponseDto {
        log.info { "Получение задач лля доски проекта с ID: $projectId" }

        projectUserUtils.checkParticipation(accessToken, projectId)

        val tasks = taskRepository.findAllByProjectUuid(projectId, Pageable.unpaged())
        val dashboardTasks = DashboardTasksResponseDto()

        tasks.forEach { task -> dashboardTasks.getTasksByStatus(task.status).add(task.toDto()) }

        return dashboardTasks
    }

    fun addTask(accessToken: String, taskDto: CreateTaskRequestDto): TaskDto {
        log.info { "Добавление задачи '${taskDto.title}'" }
        val project = projectRepository.findByUuid(taskDto.projectId)

        if (project == null) {
            log.error { "Проект с ID ${taskDto.projectId} не найден!" }
            throw ProjectNotFoundException(taskDto.projectId)
        }

        projectUserUtils.checkParticipation(accessToken, taskDto.projectId)

        val keyNumber = taskRepository.countByProjectId(project.id!!).toInt() + 1
        val author = userRepository.findByUuid(taskDto.authorId)

        if (author == null) {
            log.error { "Автор не найден" }
            throw UserNotFoundException()
        }

        val executor = taskDto.executorId?.let { userRepository.findByUuid(it) }

        val newTask = Task(
            project = project,
            title = taskDto.title,
            description = taskDto.description,
            key = "${project.key}-${keyNumber}",
            type = taskDto.type,
            status = TaskStatus.OPEN,
            priority = taskDto.priority,
            author = author,
            executor = executor,
            createdDate = LocalDateTime.now().format(formatter),
        )

        val savedTask = taskRepository.save(newTask)
        log.info { "Задача '${taskDto.title}' добавлена" }

        return savedTask.toDto()
    }

    @Transactional
    fun updateTask(accessToken: String, taskId: UUID, taskDto: UpdateTaskRequestDto): TaskDto {
        log.info { "Обновление задачи c ID: $taskId" }
        val task = taskRepository.findByUuid(taskId)

        if (task == null) {
            log.error { "Задача с ID: $taskId не найдена" }
            throw TaskNotFoundException(taskId)
        }

        projectUserUtils.checkParticipation(accessToken, task.project!!.uuid)

        val executor = when (taskDto.executorId) {
            null -> task.executor
            "" -> null
            else -> userRepository.findByUuid(UUID.fromString(taskDto.executorId))
        }

        task.title = taskDto.title ?: task.title
        task.description = taskDto.description ?: task.description
        task.type = taskDto.type ?: task.type
        task.status = taskDto.status ?: task.status
        task.priority = taskDto.priority ?: task.priority
        task.executor = executor
        task.updatedDate = LocalDateTime.now().format(formatter)
        val updatedTask = taskRepository.save(task)
        log.info { "Задача c ID: $taskId обновлена" }

        return updatedTask.toDto()
    }

    fun deleteTask(accessToken: String, taskId: UUID) {
        log.info { "Удаление задачи c ID: $taskId" }
        val task = taskRepository.findByUuid(taskId)

        if (task == null) {
            log.error { "Задача с ID: $taskId не найдена" }
            throw TaskNotFoundException(taskId)
        }

        projectUserUtils.checkAdminAccess(accessToken, task.project!!.uuid)

        taskRepository.delete(task)

        log.info { "Задача $taskId удалена" }
    }
}