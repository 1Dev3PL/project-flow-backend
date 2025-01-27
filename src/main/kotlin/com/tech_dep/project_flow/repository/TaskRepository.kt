package com.tech_dep.project_flow.repository

import com.tech_dep.project_flow.entity.Task
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository : JpaRepository<Task, Long> {
    fun countByProjectId(projectId: Long): Long

    fun findAllByProjectId(projectId: Long, pageable: Pageable): Page<Task>
}