package com.tech_dep.project_flow.dto

import jakarta.validation.constraints.NotBlank

data class UpdateProjectRequestDto(
    @field:NotBlank(message = "title is required!")
    val title: String,
    val description: String,
    val key: String,
)