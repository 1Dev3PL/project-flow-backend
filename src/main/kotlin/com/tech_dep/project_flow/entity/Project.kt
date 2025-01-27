package com.tech_dep.project_flow.entity

import com.tech_dep.project_flow.dto.ProjectDto
import jakarta.persistence.*

@Table(name = "projects")
@Entity
data class Project(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(nullable = false)
    var name: String,
    @Column(columnDefinition = "TEXT")
    var description: String,
    @Column(nullable = false)
    var key: String,
)

fun Project.toDto(): ProjectDto = ProjectDto(this.name, this.description, this.key)