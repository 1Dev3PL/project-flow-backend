package com.tech_dep.project_flow.dto

import com.tech_dep.project_flow.enums.UserRole
import jakarta.validation.constraints.Positive

data class AddUserRequestDto(
    @field:Positive(message = "userId must be positive!")
    val userId: Long,
    val role: UserRole,
)