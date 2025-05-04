package com.tech_dep.project_flow.controller

import com.tech_dep.project_flow.dto.*
import com.tech_dep.project_flow.service.TaskService
import jakarta.validation.constraints.Positive
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.UUID

@Validated
@RestController
@RequestMapping("/api/tasks")
class TaskController(
    val taskService: TaskService,
) {
    @GetMapping
    fun getTasksByProjectId(
        @CookieValue(name = "accessToken") accessToken: String,
        @RequestParam projectId: UUID,
        @Positive @RequestParam(defaultValue = "1") page: Int,
        @Positive @RequestParam(defaultValue = "10") size: Int,
        @RequestParam sortOrder: Sort.Direction?,
        @RequestParam sortBy: String?
    ): ResponseEntity<List<TaskDto>> {
        return ResponseEntity.ok(taskService.getTasksByProjectId(accessToken, projectId, page, size, sortOrder, sortBy))
    }

    @GetMapping("/dashboard")
    fun getTasksForDashboard(
        @CookieValue(name = "accessToken") accessToken: String,
        @RequestParam projectId: UUID,
    ): ResponseEntity<DashboardTasksResponseDto> {
        return ResponseEntity.ok(taskService.getTasksForDashboard(accessToken, projectId))
    }

    @PostMapping
    fun addTask(
        @CookieValue(name = "accessToken") accessToken: String,
        @RequestBody @Validated task: CreateTaskRequestDto
    ): ResponseEntity<TaskDto> {
        return ResponseEntity.ok(taskService.addTask(accessToken, task))
    }

    @GetMapping("/{id}")
    fun getTaskById(
        @CookieValue(name = "accessToken") accessToken: String,
        @PathVariable(value = "id") taskId: UUID
    ): ResponseEntity<TaskDto> {
        return ResponseEntity.ok(taskService.getTaskById(accessToken, taskId))
    }

    @PatchMapping("/{id}")
    fun updateTask(
        @CookieValue(name = "accessToken") accessToken: String,
        @PathVariable(value = "id") taskId: UUID,
        @RequestBody taskDto: UpdateTaskRequestDto
    ): ResponseEntity<TaskDto> {
        return ResponseEntity.ok(taskService.updateTask(accessToken, taskId, taskDto))
    }

    @DeleteMapping("/{id}")
    fun deleteTask(
        @CookieValue(name = "accessToken") accessToken: String,
        @PathVariable(value = "id") taskId: UUID
    ): ResponseEntity<TaskDto> {
        taskService.deleteTask(accessToken, taskId)
        return ResponseEntity.noContent().build()
    }
}