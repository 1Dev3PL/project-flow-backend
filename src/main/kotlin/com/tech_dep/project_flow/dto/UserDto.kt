package com.tech_dep.project_flow.dto

import java.util.UUID

data class UserDto(
    val id: UUID,
    val name: String,
    val email: String,
)