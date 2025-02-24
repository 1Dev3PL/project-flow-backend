package com.tech_dep.project_flow.exception

import java.util.*

class ProjectNotFoundException(projectId: UUID) : ResourceNotFoundException("Project with ID: $projectId does not found")