package com.tech_dep.project_flow.dto

import com.tech_dep.project_flow.enums.TaskStatus
import java.util.*

data class TaskPositionRequestDto(
    val taskId: UUID,
    val status: TaskStatus?,
    val afterId: UUID?,
    val beforeId: UUID?
)