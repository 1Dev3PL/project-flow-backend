package com.tech_dep.project_flow.dto

import com.tech_dep.project_flow.enums.UserRole
import java.util.*

data class AddUserRequestDto(
    val userId: UUID,
    val role: UserRole,
)