package com.tech_dep.project_flow.entity

import com.tech_dep.project_flow.enums.TaskPriority
import com.tech_dep.project_flow.enums.TaskStatus
import com.tech_dep.project_flow.enums.TaskType
import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.util.*

@Table(name = "tasks")
@Entity
data class Task(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(unique = true, nullable = false)
    var uuid: UUID = UUID.randomUUID(),
    @Column(nullable = false)
    var key: String,
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    var project: Project? = null,
    @Column(nullable = false)
    var title: String,
    @Column(columnDefinition = "TEXT")
    var description: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: TaskType,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var priority: TaskPriority,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: TaskStatus,
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    var author: User,
    @ManyToOne
    @JoinColumn(name = "executor_id")
    var executor: User? = null,
    @Column(name = "created_date", nullable = false, updatable = false)
    var createdDate: String,
    @Column(name = "updated_date")
    var updatedDate: String? = null
)