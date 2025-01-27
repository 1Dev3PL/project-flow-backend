package com.tech_dep.project_flow.controller

import com.tech_dep.project_flow.dto.*
import com.tech_dep.project_flow.entity.Project
import com.tech_dep.project_flow.service.ProjectService
import jakarta.validation.constraints.Positive
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/projects")
@Validated
class ProjectController(private val projectService: ProjectService) {
    @PostMapping
    fun createProject(
        @Positive @RequestParam(value = "userId") userId: Long,
        @RequestBody @Validated projectDto: ProjectDto
    ): ResponseEntity<Project> {
        return ResponseEntity(projectService.createProject(userId, projectDto), HttpStatus.CREATED)
    }

    // TODO - Pagination
    @GetMapping
    fun getAllProjects(@Positive @RequestParam(value = "userId") userId: Long): ResponseEntity<List<Project>> {
        return ResponseEntity.ok(projectService.getAllProjects(userId))
    }

    @GetMapping("/{id}")
    fun getProjectDto(@Positive @PathVariable(name = "id") projectId: Long): ResponseEntity<ProjectDto> {
        return ResponseEntity.ok(projectService.getProjectDto(projectId))
    }

    @PutMapping("/{id}")
    fun updateProject(
        @Positive @PathVariable(name = "id") projectId: Long,
        @RequestBody @Validated projectDto: ProjectDto
    ): ResponseEntity<ProjectDto> {
        return ResponseEntity.ok(projectService.updateProject(projectId, projectDto))
    }

    @DeleteMapping("/{id}")
    fun deleteProject(@Positive @PathVariable id: Long): ResponseEntity<Void> {
        projectService.deleteProject(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/users")
    fun addUser(
        @Positive @PathVariable(name = "id") projectId: Long,
        @RequestBody @Validated userData: AddUserRequestDto
    ): ResponseEntity<MessageResponseDto> {
        return ResponseEntity.ok(projectService.addUser(projectId, userData))
    }

    @GetMapping("/{id}/users")
    fun getUsers(
        @Positive @PathVariable(name = "id") projectId: Long,
    ): ResponseEntity<List<UserDto>> {
        return ResponseEntity.ok(projectService.getUsers(projectId))
    }

    @PostMapping("/{projectId}/users/{userId}")
    fun changeUserRole(
        @Positive @PathVariable projectId: Long,
        @Positive @PathVariable userId: Long,
        @RequestBody roleData: ChangeUserRoleRequestDto
    ): ResponseEntity<MessageResponseDto> {
        return ResponseEntity.ok(projectService.changeUserRole(projectId, userId, roleData))
    }
}