package com.tech_dep.project_flow.repository

import com.tech_dep.project_flow.entity.Project
import com.tech_dep.project_flow.entity.ProjectUser
import com.tech_dep.project_flow.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProjectUserRepository : JpaRepository<ProjectUser, Long> {
    @Query("SELECT p FROM ProjectUser pu JOIN pu.project p WHERE pu.user.uuid = :userId")
    fun findProjectsByUserUuid(userId: UUID): List<Project>

    @Query("SELECT u FROM ProjectUser pu JOIN pu.user u WHERE pu.project.uuid = :projectId")
    fun findUsersByProjectUuid(projectId: UUID): List<User>

    fun findByProjectUuidAndUserUuid(projectId: UUID, userId: UUID): ProjectUser?
}