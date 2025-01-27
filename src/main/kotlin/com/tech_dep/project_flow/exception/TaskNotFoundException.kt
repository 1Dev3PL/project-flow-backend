package com.tech_dep.project_flow.exception

class TaskNotFoundException(taskId: Long) : ResourceNotFoundException("Task with ID: $taskId does not found")