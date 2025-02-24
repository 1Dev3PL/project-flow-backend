package com.tech_dep.project_flow.dto

import com.tech_dep.project_flow.enums.TaskPriority
import com.tech_dep.project_flow.enums.TaskType
import jakarta.validation.constraints.NotBlank
import java.util.*

data class CreateTaskRequestDto(
    val projectId: UUID,
    @field:NotBlank(message = "title is required!")
    val title: String,
    val description: String,
    val type: TaskType,
    val priority: TaskPriority,
    val authorId: UUID,
    val executorId: UUID?,
)
