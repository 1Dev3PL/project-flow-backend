package com.tech_dep.project_flow.entity

import com.tech_dep.project_flow.dto.TaskDto
import com.tech_dep.project_flow.enums.TaskPriority
import com.tech_dep.project_flow.enums.TaskStatus
import com.tech_dep.project_flow.enums.TaskType
import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.LocalDateTime

@Table(name = "tasks")
@Entity
data class Task(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
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
    @Column(name = "author_id", nullable = false)
    var authorId: Long,
    @Column(name = "executor_id")
    var executorId: Long? = null,
    @Column(name = "created_date", nullable = false, updatable = false)
    var createdDate: LocalDateTime,
    @Column(name = "updated_date")
    var updatedDate: LocalDateTime
)

fun Task.toDto(): TaskDto = TaskDto(
    key = this.key,
    projectId = this.project?.id!!,
    title = this.title,
    description = this.description,
    type = this.type,
    priority = this.priority,
    status = this.status,
    authorId = this.authorId,
    executorId = this.executorId,
    createdDate = this.createdDate,
    updatedDate = this.updatedDate,
)