package com.tech_dep.project_flow.repository

import com.tech_dep.project_flow.entity.Project
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProjectRepository : JpaRepository<Project, Long> {
    fun findByUuid(uuid: UUID): Project?

    fun deleteByUuid(uuid: UUID)
}