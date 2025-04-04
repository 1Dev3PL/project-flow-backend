package com.tech_dep.project_flow.entity

import com.tech_dep.project_flow.enums.UserRole
import jakarta.persistence.*

@Table(name = "projects_to_users")
@Entity
data class ProjectUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false)
    var project: Project? = null,
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    var user: User? = null,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: UserRole,
)