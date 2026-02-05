package com.hadiubaidillah.service.todo.entity

import jakarta.persistence.*
import java.io.Serializable
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "tasks")
data class Task(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val name: String,

    @Column(columnDefinition = "TEXT")
    val description: String? = null,

    @Column(nullable = false)
    val completed: Boolean = false,

    @Column(nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now(),

    val endsIn: OffsetDateTime? = null,

    @Column(nullable = false)
    val overdueNotified: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    val author: TaskAuthor? = null
) : Serializable {

    fun isOverdue(): Boolean = !completed && endsIn != null && endsIn.isBefore(OffsetDateTime.now())

    fun isInProgress(): Boolean = !completed && (endsIn == null || !endsIn.isBefore(OffsetDateTime.now()))
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
