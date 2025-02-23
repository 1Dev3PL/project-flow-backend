package com.tech_dep.project_flow.dto

import com.tech_dep.project_flow.enums.TaskPriority
import com.tech_dep.project_flow.enums.TaskType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class CreateTaskRequestDto(
    @field:Positive(message = "projectId is required and must be positive!")
    val projectId: Long,
    @field:NotBlank(message = "title is required!")
    val title: String,
    val description: String,
    val type: TaskType,
    val priority: TaskPriority,
    @field:Positive(message = "authorId is required and must be positive!")
    val authorId: Long,
    @field:Positive(message = "executorId must be positive!")
    val executorId: Long?,
)
