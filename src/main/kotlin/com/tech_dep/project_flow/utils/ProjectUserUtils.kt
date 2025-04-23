package com.tech_dep.project_flow.utils

import com.tech_dep.project_flow.enums.UserRole
import com.tech_dep.project_flow.exception.AccessDeniedException
import com.tech_dep.project_flow.repository.ProjectUserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProjectUserUtils(
    private val projectUserRepository: ProjectUserRepository,
    private val jwtUtils: JwtUtils,
) {
    val log = KotlinLogging.logger {}

    fun checkParticipation(accessToken: String, projectId: UUID) {
        val clientId = jwtUtils.extractId(accessToken)

        if (projectUserRepository.findByProjectUuidAndUserUuid(projectId, clientId) == null) {
            log.error { "Пользователь $clientId не имеет прав доступа к запрашиваемому ресурсу" }
            throw AccessDeniedException("User does not have access to the requested resource")
        }
    }

    fun checkAdminAccess(accessToken: String, projectId: UUID) {
        val clientId = jwtUtils.extractId(accessToken)

        if (projectUserRepository.findByProjectUuidAndUserUuid(projectId, clientId)?.role != UserRole.ADMIN) {
            log.error { "Пользователь $clientId не имеет прав доступа к запрашиваемому ресурсу" }
            throw AccessDeniedException("User does not have access to the requested resource")
        }
    }
}