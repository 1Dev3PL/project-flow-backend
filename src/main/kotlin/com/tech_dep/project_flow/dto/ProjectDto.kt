package com.tech_dep.project_flow.dto

import java.util.*

data class ProjectDto(
    val id: UUID,
    val title: String,
    val description: String,
    val key: String,
)
