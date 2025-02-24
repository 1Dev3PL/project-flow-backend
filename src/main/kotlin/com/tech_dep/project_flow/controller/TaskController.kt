package com.tech_dep.project_flow.controller

import com.tech_dep.project_flow.dto.*
import com.tech_dep.project_flow.service.TaskService
import jakarta.validation.constraints.Positive
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
        @RequestParam projectId: UUID,
        @Positive @RequestParam(defaultValue = "1") page: Int,
        @Positive @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<TasksByProjectResponseDto> {
        return ResponseEntity.ok(taskService.getTasksByProjectId(projectId, page, size))
    }

    @PostMapping
    fun addTask(@RequestBody @Validated task: CreateTaskRequestDto): ResponseEntity<TaskDto> {
        return ResponseEntity.ok(taskService.addTask(task))
    }

    @GetMapping("/{id}")
    fun getTaskById(@PathVariable(value = "id") taskId: UUID): ResponseEntity<TaskDto> {
        return ResponseEntity.ok(taskService.getTaskById(taskId))
    }

    @PutMapping("/{id}")
    fun updateTask(
        @PathVariable(value = "id") taskId: UUID,
        @RequestBody @Validated taskDto: UpdateTaskRequestDto
    ): ResponseEntity<TaskDto> {
        return ResponseEntity.ok(taskService.updateTask(taskId, taskDto))
    }
}