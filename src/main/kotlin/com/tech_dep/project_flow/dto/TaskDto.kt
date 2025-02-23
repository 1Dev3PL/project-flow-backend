package com.tech_dep.project_flow.dto

import com.tech_dep.project_flow.enums.TaskPriority
import com.tech_dep.project_flow.enums.TaskStatus
import com.tech_dep.project_flow.enums.TaskType

data class TaskDto(
    val id: Long,
    val projectId: Long,
    val key: String,
    val title: String,
    val description: String,
    val type: TaskType,
    val priority: TaskPriority,
    val status: TaskStatus,
    val authorId: Long,
    val executorId: Long?,
    val createdDate: String,
    val updatedDate: String?,
)
