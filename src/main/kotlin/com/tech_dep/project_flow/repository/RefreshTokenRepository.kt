package com.tech_dep.project_flow.repository

import com.tech_dep.project_flow.entity.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
    fun findByToken(token: String): RefreshToken?

    fun existsByUserId(userId: Long): Boolean

    fun deleteByUserId(userId: Long): Long
}