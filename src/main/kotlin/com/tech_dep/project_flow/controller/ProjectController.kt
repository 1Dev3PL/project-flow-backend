package com.tech_dep.project_flow.controller

import com.tech_dep.project_flow.dto.*
import com.tech_dep.project_flow.exception.UserAlreadyInProjectException
import com.tech_dep.project_flow.service.ProjectService
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
        @RequestParam(value = "userId") userId: UUID,
        @RequestBody @Validated createProjectRequest: CreateProjectRequestDto
    ): ResponseEntity<ProjectDto> {
        return ResponseEntity(projectService.createProject(userId, createProjectRequest), HttpStatus.CREATED)
    }

    // TODO - Pagination
    @GetMapping
    fun getAllProjects(@RequestParam(value = "userId") userId: UUID): ResponseEntity<List<ProjectDto>> {
        return ResponseEntity.ok(projectService.getAllProjects(userId))
    }

    @GetMapping("/{id}")
    fun getProject(@PathVariable(name = "id") projectId: UUID): ResponseEntity<ProjectDto> {
        return ResponseEntity.ok(projectService.getProject(projectId))
    }

    @PutMapping("/{id}")
    fun updateProject(
        @PathVariable(name = "id") projectId: UUID,
        @RequestBody @Validated updateProjectRequest: UpdateProjectRequestDto
    ): ResponseEntity<ProjectDto> {
        return ResponseEntity.ok(projectService.updateProject(projectId, updateProjectRequest))
    }

    @DeleteMapping("/{id}")
    fun deleteProject(@PathVariable id: UUID): ResponseEntity<Void> {
        projectService.deleteProject(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/users")
    fun addUser(
        @PathVariable(name = "id") projectId: UUID,
        @RequestBody userData: AddUserRequestDto
    ): ResponseEntity<MessageResponseDto> {
        return ResponseEntity.ok(projectService.addUser(projectId, userData))
    }

    @GetMapping("/{id}/users")
    fun getUsers(
        @PathVariable(name = "id") projectId: UUID,
    ): ResponseEntity<List<UserDto>> {
        return ResponseEntity.ok(projectService.getUsers(projectId))
    }

    @PostMapping("/{projectId}/users/{userId}")
    fun changeUserRole(
        @PathVariable projectId: UUID,
        @PathVariable userId: UUID,
        @RequestBody roleData: ChangeUserRoleRequestDto
    ): ResponseEntity<MessageResponseDto> {
        return ResponseEntity.ok(projectService.changeUserRole(projectId, userId, roleData))
    }
}