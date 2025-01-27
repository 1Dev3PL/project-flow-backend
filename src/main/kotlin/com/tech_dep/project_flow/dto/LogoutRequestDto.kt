package com.tech_dep.project_flow.dto

import jakarta.validation.constraints.NotBlank

data class LogoutRequestDto(
    @field:NotBlank(message = "email is required!")
    val email: String
)