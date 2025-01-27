package com.tech_dep.project_flow.entity

import com.tech_dep.project_flow.enums.UserRole
import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

@Table(name = "projects_and_users")
@Entity
data class ProjectUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    val project: Project? = null,
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    val user: User? = null,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: UserRole,
)