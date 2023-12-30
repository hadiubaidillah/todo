package me.sknz.ms.tasks.notification.entity

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import java.time.Clock
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "ms_notifications")
class TaskNotification {

    @Id
    var id: UUID? = null
    var readed: Boolean = false
    var sent: Boolean = false

    lateinit var type: NotificationType

    @ManyToOne
    @JoinColumn(name = "task_id")
    lateinit var task: Task

    @JsonProperty("created_at")
    var createdAt = OffsetDateTime.now(Clock.systemUTC())

    enum class NotificationType {
        CREATION,
        DELETION,
        FINISHED
    }
}