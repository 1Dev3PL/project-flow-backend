package com.tech_dep.project_flow.exception

import java.util.*

class ProjectUserNotFoundException(projectId: UUID, userId: UUID) :
    ResourceNotFoundException("User with ID: $userId does not participate in project $projectId")