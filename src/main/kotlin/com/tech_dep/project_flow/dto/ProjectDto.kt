package com.tech_dep.project_flow.dto

import jakarta.validation.constraints.NotBlank

data class ProjectDto(
    @field:NotBlank(message = "name is required!")
    val title: String,
    val description: String,
    val key: String,
)
