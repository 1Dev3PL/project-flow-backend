package com.tech_dep.project_flow.service

import com.tech_dep.project_flow.dto.CreateTaskRequestDto
import com.tech_dep.project_flow.dto.TaskDto
import com.tech_dep.project_flow.dto.TasksByProjectResponseDto
import com.tech_dep.project_flow.dto.UpdateTaskRequestDto
import com.tech_dep.project_flow.entity.Task
import com.tech_dep.project_flow.entity.toDto
import com.tech_dep.project_flow.enums.TaskStatus
import com.tech_dep.project_flow.exception.ProjectNotFoundException
import com.tech_dep.project_flow.exception.TaskNotFoundException
import com.tech_dep.project_flow.repository.ProjectRepository
import com.tech_dep.project_flow.repository.TaskRepository
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
) {
    val log = KotlinLogging.logger {}
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")

    // TODO - Role checking
    fun getTaskById(taskId: UUID): TaskDto {
        log.info { "Получение задачи с ID: $taskId" }
        val task = taskRepository.findByUuid(taskId)

        if (task == null) {
            log.error { "Задача с ID $taskId не найдена!" }
            throw TaskNotFoundException(taskId)
        }
        log.info { "Задача с ID: $taskId найдена" }

        return task.toDto()
    }

    fun getTasksByProjectId(
        projectId: UUID,
        page: Int,
        size: Int,
        sortOrder: Sort.Direction?,
        sortBy: String?
    ): TasksByProjectResponseDto {
        log.info { "Получение задач проекта с ID: $projectId" }

        val pageable: Pageable = if (sortOrder != null && sortBy != null) {
            PageRequest.of(page - 1, size, Sort.by(sortOrder, sortBy))
        } else {
            PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "id"))
        }

        val tasks = taskRepository.findAllByProjectUuid(projectId, pageable)
        return TasksByProjectResponseDto(
            pagesCount = tasks.totalPages,
            tasks = tasks.content.map { it.toDto() }
        )
    }

    fun addTask(taskDto: CreateTaskRequestDto): TaskDto {
        log.info { "Добавление задачи '${taskDto.title}'" }
        val project = projectRepository.findByUuid(taskDto.projectId)

        if (project == null) {
            log.error { "Проект с ID ${taskDto.projectId} не найден!" }
            throw ProjectNotFoundException(taskDto.projectId)
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

    fun updateTask(taskId: UUID, taskDto: UpdateTaskRequestDto): TaskDto {
        log.info { "Обновление задачи c ID: $taskId" }
        val task = taskRepository.findByUuid(taskId)

        if (task == null) {
            log.error { "Задача с ID: $taskId не найдена для обновления" }
            throw TaskNotFoundException(taskId)
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