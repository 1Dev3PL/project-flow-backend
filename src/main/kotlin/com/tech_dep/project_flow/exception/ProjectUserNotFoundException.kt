package com.tech_dep.project_flow.exception

class ProjectUserNotFoundException(projectId: Long, userId: Long) :
    ResourceNotFoundException("User with ID: $userId does not participate in project $projectId")