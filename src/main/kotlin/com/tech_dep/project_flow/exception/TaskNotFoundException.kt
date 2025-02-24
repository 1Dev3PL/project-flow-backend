package com.tech_dep.project_flow.exception

import java.util.*

class TaskNotFoundException(taskId: UUID) : ResourceNotFoundException("Task with ID: $taskId does not found")