package com.tech_dep.project_flow.dto

import jakarta.validation.constraints.NotBlank

data class RegisterRequestDto(
    @field:NotBlank(message = "name is required!")
    val name: String,
    @field:NotBlank(message = "email is required!")
    val email: String,
    @field:NotBlank(message = "password is required!")
    val password: String
)