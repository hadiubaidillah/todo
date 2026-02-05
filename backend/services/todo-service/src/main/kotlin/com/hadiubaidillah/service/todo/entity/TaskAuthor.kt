package com.hadiubaidillah.service.todo.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.io.Serializable
import java.util.UUID

@Entity
@Table(name = "task_authors")
data class TaskAuthor(
    @Id
    val id: UUID,

    @Column(nullable = false)
    val email: String,

    val firstName: String? = null,

    val lastName: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
