package com.tech_dep.project_flow.repository

import com.tech_dep.project_flow.entity.Task
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TaskRepository : JpaRepository<Task, Long> {
    fun findByUuid(uuid: UUID): Task?

    fun countByProjectId(projectId: Long): Long

    fun findAllByProjectUuid(projectId: UUID, pageable: Pageable): Page<Task>
}