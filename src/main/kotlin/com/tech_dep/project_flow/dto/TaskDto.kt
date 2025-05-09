package com.tech_dep.project_flow.dto

import com.tech_dep.project_flow.entity.Task
import com.tech_dep.project_flow.enums.TaskPriority
import com.tech_dep.project_flow.enums.TaskStatus
import com.tech_dep.project_flow.enums.TaskType
import java.util.*

data class TaskDto(
    val id: UUID,
    val project: ProjectDto,
    val key: String,
    val title: String,
    val description: String,
    val type: TaskType,
    val priority: TaskPriority,
    val status: TaskStatus,
    val author: UserDto,
    val executor: UserDto?,
    val createdDate: String,
    val updatedDate: String?,
)

fun Task.toDto(): TaskDto = TaskDto(
    id = this.uuid,
    key = this.key,
    project = this.project.toDto(),
    title = this.title,
    description = this.description,
    type = this.type,
    priority = this.priority,
    status = this.status,
    author = this.author.toDto(),
    executor = this.executor?.toDto(),
    createdDate = this.createdDate,
    updatedDate = this.updatedDate,
)