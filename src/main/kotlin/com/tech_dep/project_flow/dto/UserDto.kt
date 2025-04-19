package com.tech_dep.project_flow.dto

import com.tech_dep.project_flow.entity.User
import java.util.UUID

data class UserDto(
    val id: UUID,
    val name: String,
    val email: String,
)

fun User.toDto(): UserDto = UserDto(id = this.uuid, name = this.name, email = this.email)