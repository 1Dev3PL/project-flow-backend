package com.tech_dep.project_flow.dto

import jakarta.validation.constraints.NotBlank

data class LoginRequestDto(
    @field:NotBlank(message = "email is required!")
    val email: String,
    @field:NotBlank(message = "password is required!")
    val password: String
)