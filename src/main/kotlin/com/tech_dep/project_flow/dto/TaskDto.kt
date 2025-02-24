package com.tech_dep.project_flow.dto

import com.tech_dep.project_flow.enums.TaskPriority
import com.tech_dep.project_flow.enums.TaskStatus
import com.tech_dep.project_flow.enums.TaskType
import java.util.*

data class TaskDto(
    val id: UUID,
    val projectId: UUID,
    val key: String,
    val title: String,
    val description: String,
    val type: TaskType,
    val priority: TaskPriority,
    val status: TaskStatus,
    val authorId: UUID,
    val executorId: UUID?,
    val createdDate: String,
    val updatedDate: String?,
)
