package com.tech_dep.project_flow.dto

import com.tech_dep.project_flow.enums.TaskStatus
import kotlin.reflect.full.memberProperties

data class DashboardTasksResponseDto(
    val open: MutableList<TaskDto> = mutableListOf(),
    val in_progress: MutableList<TaskDto> = mutableListOf(),
    val review: MutableList<TaskDto> = mutableListOf(),
    val testing: MutableList<TaskDto> = mutableListOf(),
    val complete: MutableList<TaskDto> = mutableListOf(),
)

fun DashboardTasksResponseDto.getTasksByStatus(status: TaskStatus): MutableList<TaskDto> {
    @Suppress("UNCHECKED_CAST")
    return this::class.memberProperties
        .first { it.name == status.name.lowercase() }.getter.call(this) as MutableList<TaskDto>
}