package com.tech_dep.project_flow.dto

import jakarta.validation.constraints.NotBlank

data class CreateProjectRequestDto(
    @field:NotBlank(message = "title is required!")
    val title: String,
    val description: String,
    val key: String,
)