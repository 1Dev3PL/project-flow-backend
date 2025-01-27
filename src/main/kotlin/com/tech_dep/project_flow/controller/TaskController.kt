package com.tech_dep.project_flow.controller

import com.tech_dep.project_flow.dto.*
import com.tech_dep.project_flow.entity.Task
import com.tech_dep.project_flow.service.TaskService
import jakarta.validation.constraints.Positive
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping("/api/tasks")
class TaskController(
    val taskService: TaskService,
) {
    @GetMapping
    fun getTasksByProjectId(
        @Positive @RequestParam projectId: Long,
        @Positive @RequestParam(defaultValue = "1") page: Int,
        @Positive @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<TasksByProjectResponseDto> {
        return ResponseEntity.ok(taskService.getTasksByProjectId(projectId, page, size))
    }

    @PostMapping
    fun addTask(@RequestBody @Validated task: CreateTaskRequestDto): ResponseEntity<Task> {
        return ResponseEntity.ok(taskService.addTask(task))
    }

    @GetMapping("/{id}")
    fun getTaskById(@Positive @PathVariable(value = "id") taskId: Long): ResponseEntity<TaskDto> {
        return ResponseEntity.ok(taskService.getTaskById(taskId))
    }

    @PutMapping("/{id}")
    fun updateTask(
        @Positive @PathVariable(value = "id") taskId: Long,
        @RequestBody @Validated taskDto: UpdateTaskRequestDto
    ): ResponseEntity<TaskDto> {
        return ResponseEntity.ok(taskService.updateTask(taskId, taskDto))
    }
}