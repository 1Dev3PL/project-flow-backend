package com.tech_dep.project_flow.dto

import com.tech_dep.project_flow.enums.TaskPriority
import com.tech_dep.project_flow.enums.TaskStatus
import com.tech_dep.project_flow.enums.TaskType
import java.time.LocalDateTime

data class TaskDto(
    val projectId: Long,
    val key: String,
    val title: String,
    val description: String,
    val type: TaskType,
    val priority: TaskPriority,
    val status: TaskStatus,
    val authorId: Long,
    val executorId: Long?,
    val createdDate: LocalDateTime,
    val updatedDate: LocalDateTime,
)
