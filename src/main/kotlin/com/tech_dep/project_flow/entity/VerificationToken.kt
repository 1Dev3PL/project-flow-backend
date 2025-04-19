package com.tech_dep.project_flow.entity

import jakarta.persistence.*
import java.time.Instant

@Table(name = "verification_token")
@Entity
data class VerificationToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false)
    val token: String,
    @Column(nullable = false)
    val expiryDate: Instant,
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    val user: User,
)