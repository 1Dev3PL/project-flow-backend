package com.tech_dep.project_flow.repository

import com.tech_dep.project_flow.entity.VerificationToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VerificationTokenRepository : JpaRepository<VerificationToken, Long> {
    fun findByToken(token: String) : VerificationToken?
}