package com.tech_dep.project_flow.dto

import com.tech_dep.project_flow.entity.Task

data class TasksByProjectResponseDto(
    val pagesCount: Int,
    val tasks: List<Task>,
)