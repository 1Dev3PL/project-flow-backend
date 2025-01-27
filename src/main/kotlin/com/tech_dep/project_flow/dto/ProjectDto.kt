package com.tech_dep.project_flow.dto

import jakarta.validation.constraints.NotBlank

data class ProjectDto(
    @field:NotBlank(message = "name is required!")
    val name: String,
    val description: String,
    @field:NotBlank(message = "key is required!")
    val key: String,
)
