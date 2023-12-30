package me.sknz.ms.tasks.notification.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import me.sknz.ms.tasks.notification.entity.Task
import me.sknz.ms.tasks.notification.entity.TaskAuthor
import java.time.Clock
import java.time.OffsetDateTime
import java.util.*

class TaskModel {

    @Id
    lateinit var id: UUID
    lateinit var name: String
    var description: String? = null
    lateinit var author: TaskAuthorModel

    @JsonProperty("created_at")
    lateinit var createdAt: OffsetDateTime
    @JsonProperty("ends_in")
    lateinit var endsIn: OffsetDateTime
    var completed: Boolean = false

    fun toTask(author: TaskAuthor): Task {
        val task = Task()

        task.id = this.id
        task.name = this.name
        task.description = this.description
        task.createdAt = this.createdAt
        task.author = author
        task.endsIn = this.endsIn
        task.completed = this.completed

        return task
    }
}