package com.tech_dep.project_flow.dto

import com.tech_dep.project_flow.enums.UserRole
import java.util.*

data class UserWithRoleDto(
    val id: UUID,
    val name: String,
    val email: String,
    val role: UserRole
)