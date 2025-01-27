package com.tech_dep.project_flow.exception

class ProjectNotFoundException(projectId: Long) : ResourceNotFoundException("Project with ID: $projectId does not found")