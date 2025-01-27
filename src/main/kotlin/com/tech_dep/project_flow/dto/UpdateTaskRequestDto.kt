package com.tech_dep.project_flow.dto

import com.tech_dep.project_flow.enums.TaskPriority
import com.tech_dep.project_flow.enums.TaskStatus
import com.tech_dep.project_flow.enums.TaskType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class UpdateTaskRequestDto(
    @field:NotBlank(message = "title is required!")
    val title: String,
    val description: String,
    val type: TaskType,
    val priority: TaskPriority,
    val status: TaskStatus,
    @field:Positive(message = "executorId must be positive!")
    val executorId: Long?,
)