package com.tech_dep.project_flow.dto

import com.tech_dep.project_flow.entity.Project
import java.util.*

data class ProjectDto(
    val id: UUID,
    val title: String,
    val description: String,
    val key: String,
)

fun Project.toDto(): ProjectDto = ProjectDto(id = this.uuid, this.title, this.description, this.key)