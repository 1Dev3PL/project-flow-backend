package com.tech_dep.project_flow.dto

import com.tech_dep.project_flow.enums.TaskPriority
import com.tech_dep.project_flow.enums.TaskStatus
import com.tech_dep.project_flow.enums.TaskType
import java.util.*

data class UpdateTaskRequestDto(
    val title: String?,
    val description: String?,
    val type: TaskType?,
    val priority: TaskPriority?,
    val status: TaskStatus?,
    val executorId: UUID?,
)