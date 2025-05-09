package com.tech_dep.project_flow.repository

import com.tech_dep.project_flow.entity.Task
import com.tech_dep.project_flow.enums.TaskStatus
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TaskRepository : JpaRepository<Task, Long> {
    fun findByUuid(uuid: UUID): Task?

    fun countByProjectId(projectId: Long): Long

    fun findAllByProjectUuid(projectId: UUID, pageable: Pageable): List<Task>

    fun findAllByProjectUuidOrderByRank(projectId: UUID): List<Task>

    @Query("SELECT t.rank FROM Task t WHERE t.project.uuid = :projectId AND t.status = :status ORDER BY t.rank DESC LIMIT 1")
    fun findLastRank(projectId: UUID, status: TaskStatus): String?
}