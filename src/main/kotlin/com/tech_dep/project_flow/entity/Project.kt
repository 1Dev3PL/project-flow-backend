package com.tech_dep.project_flow.entity

import com.tech_dep.project_flow.dto.ProjectDto
import jakarta.persistence.*
import java.util.UUID

@Table(name = "projects")
@Entity
data class Project(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(unique = true, nullable = false)
    var uuid: UUID = UUID.randomUUID(),
    @Column(nullable = false)
    var title: String,
    @Column(columnDefinition = "TEXT")
    var description: String,
    @Column(nullable = false)
    var key: String,
    @Column(nullable = false)
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    var users: MutableList<ProjectUser> = mutableListOf(),
)

fun Project.toDto(): ProjectDto = ProjectDto(id = this.uuid, this.title, this.description, this.key)

fun Project.addUser(user: ProjectUser) {
    users.add(user)
    user.project = this
}

fun Project.removeUser(user: ProjectUser) {
    users.remove(user)
    user.project = null
}