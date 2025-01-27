package com.tech_dep.project_flow.dto

import com.tech_dep.project_flow.enums.UserRole

data class ChangeUserRoleRequestDto(
    val role: UserRole
)