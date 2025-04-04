package com.tech_dep.project_flow.controller

import com.tech_dep.project_flow.dto.*
import com.tech_dep.project_flow.exception.UserAlreadyInProjectException
import com.tech_dep.project_flow.service.ProjectService
import jakarta.validation.constraints.Positive
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/projects")
@Validated
class ProjectController(private val projectService: ProjectService) {
    @ExceptionHandler(UserAlreadyInProjectException::class)
    fun handleUserAlreadyInProjectException(ex: Exception): ResponseEntity<MessageResponseDto> {
        val response = MessageResponseDto(ex.message!!, false)
        return ResponseEntity<MessageResponseDto>(response, HttpStatus.CONFLICT)
    }

    @PostMapping
    fun createProject(
        @CookieValue(name = "accessToken") accessToken: String,
        @RequestBody @Validated createProjectRequest: CreateProjectRequestDto
    ): ResponseEntity<ProjectDto> {
        return ResponseEntity(projectService.createProject(accessToken, createProjectRequest), HttpStatus.CREATED)
    }

    // TODO - Pagination
    @GetMapping
    fun getAllProjects(@CookieValue(name = "accessToken") accessToken: String): ResponseEntity<List<ProjectDto>> {
        return ResponseEntity.ok(projectService.getAllProjects(accessToken))
    }

    @GetMapping("/{id}")
    fun getProject(
        @CookieValue(name = "accessToken") accessToken: String,
        @PathVariable(name = "id") projectId: UUID
    ): ResponseEntity<ProjectDto> {
        return ResponseEntity.ok(projectService.getProject(accessToken, projectId))
    }

    @PutMapping("/{id}")
    fun updateProject(
        @CookieValue(name = "accessToken") accessToken: String,
        @PathVariable(name = "id") projectId: UUID,
        @RequestBody @Validated updateProjectRequest: UpdateProjectRequestDto
    ): ResponseEntity<ProjectDto> {
        return ResponseEntity.ok(projectService.updateProject(accessToken, projectId, updateProjectRequest))
    }

    @DeleteMapping("/{id}")
    fun deleteProject(
        @CookieValue(name = "accessToken") accessToken: String,
        @PathVariable(name = "id") projectId: UUID
    ): ResponseEntity<Void> {
        projectService.deleteProject(accessToken, projectId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/users")
    fun addUser(
        @CookieValue(name = "accessToken") accessToken: String,
        @PathVariable(name = "id") projectId: UUID,
        @RequestBody userData: AddUserRequestDto
    ): ResponseEntity<MessageResponseDto> {
        return ResponseEntity.ok(projectService.addUser(accessToken, projectId, userData))
    }

    @GetMapping("/{id}/users")
    fun getUsers(
        @CookieValue(name = "accessToken") accessToken: String,
        @PathVariable(name = "id") projectId: UUID,
        @Positive @RequestParam(defaultValue = "1") page: Int,
        @Positive @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<List<UserWithRoleDto>> {
        return ResponseEntity.ok(projectService.getUsers(accessToken, projectId, page, size))
    }

    @PostMapping("/{projectId}/users/{userId}/role")
    fun changeUserRole(
        @CookieValue(name = "accessToken") accessToken: String,
        @PathVariable projectId: UUID,
        @PathVariable userId: UUID,
        @RequestBody roleData: ChangeUserRoleRequestDto
    ): ResponseEntity<MessageResponseDto> {
        return ResponseEntity.ok(projectService.changeUserRole(accessToken, projectId, userId, roleData))
    }

    @DeleteMapping("/{projectId}/users/{userId}")
    fun excludeUser(
        @CookieValue(name = "accessToken") accessToken: String,
        @PathVariable projectId: UUID,
        @PathVariable userId: UUID,
    ): ResponseEntity<MessageResponseDto> {
        projectService.excludeUser(accessToken, projectId, userId)
        return ResponseEntity.noContent().build()
    }
}