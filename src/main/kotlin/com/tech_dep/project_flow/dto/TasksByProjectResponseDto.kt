package com.tech_dep.project_flow.dto

data class TasksByProjectResponseDto(
    val pagesCount: Int,
    val tasks: List<TaskDto>,
)