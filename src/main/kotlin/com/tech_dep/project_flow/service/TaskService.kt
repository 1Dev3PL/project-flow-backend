package com.tech_dep.project_flow.service

import com.tech_dep.project_flow.dto.CreateTaskRequestDto
import com.tech_dep.project_flow.dto.TaskDto
import com.tech_dep.project_flow.dto.TasksByProjectResponseDto
import com.tech_dep.project_flow.dto.UpdateTaskRequestDto
import com.tech_dep.project_flow.entity.Task
import com.tech_dep.project_flow.entity.toDto
import com.tech_dep.project_flow.exception.ProjectNotFoundException
import com.tech_dep.project_flow.exception.TaskNotFoundException
import com.tech_dep.project_flow.repository.ProjectRepository
import com.tech_dep.project_flow.repository.TaskRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime


@Service
class TaskService(
    val taskRepository: TaskRepository,
    val projectRepository: ProjectRepository,
) {
    val log = KotlinLogging.logger {}
    // TODO - Role checking
    // TODO - Task contain project info, maybe add id in dto?
    fun getTaskById(taskId: Long): TaskDto {
        log.info { "Получение задачи с ID: $taskId" }
        val task = taskRepository.findByIdOrNull(taskId)

        if (task == null) {
            log.error { "Задача с ID $taskId не найдена!" }
            throw TaskNotFoundException(taskId)
        }
        log.info { "Задача с ID: $taskId найдена" }

        return task.toDto()
    }

    fun getTasksByProjectId(projectId: Long, page: Int, size: Int): TasksByProjectResponseDto {
        log.info { "Получение задач проекта с ID: $projectId" }
        val pageable: Pageable = PageRequest.of(page - 1, size)
        val tasks = taskRepository.findAllByProjectId(projectId, pageable)
        return TasksByProjectResponseDto(
            pagesCount = tasks.totalPages,
            tasks = tasks.content
        )
    }

    fun addTask(taskDto: CreateTaskRequestDto): Task {
        log.info { "Добавление задачи '${taskDto.title}'" }
        val project = projectRepository.findByIdOrNull(taskDto.projectId)

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
            status = taskDto.status,
            priority = taskDto.priority,
            authorId = taskDto.authorId,
            executorId = taskDto.executorId,
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now()
        )

        val savedTask = taskRepository.save(newTask)
        log.info { "Задача '${taskDto.title}' добавлена" }

        return savedTask
    }

    fun updateTask(taskId: Long, taskDto: UpdateTaskRequestDto): TaskDto {
        log.info { "Обновление задачи c ID: $taskId" }
        val task = taskRepository.findByIdOrNull(taskId)

        if (task == null) {
            log.error { "Задача с ID: $taskId не найдена для обновления" }
            throw TaskNotFoundException(taskId)
        }

        task.title = taskDto.title
        task.description = taskDto.description
        task.type = taskDto.type
        task.status = taskDto.status
        task.priority = taskDto.priority
        task.executorId = taskDto.executorId
        task.updatedDate = LocalDateTime.now()
        val updatedTask = taskRepository.save(task)
        log.info { "Задача c ID: $taskId обновлена" }

        return updatedTask.toDto()
    }
}