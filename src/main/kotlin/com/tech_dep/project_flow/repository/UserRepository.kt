package com.tech_dep.project_flow.repository

import com.tech_dep.project_flow.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?

    fun findByUuid(uuid: UUID): User?

    fun existsByEmail(email: String): Boolean
}