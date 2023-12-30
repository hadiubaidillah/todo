package me.sknz.ms.tasks.notification.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import java.time.Clock
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "ms_notifications_task")
class Task {

    @Id
    var id: UUID? = null
    var name: String = ""
    var description: String? = null

    @ManyToOne
    @JoinColumn(name = "author_id")
    lateinit var author: TaskAuthor

    @JsonProperty("created_at")
    var createdAt: OffsetDateTime = OffsetDateTime.now(Clock.systemUTC())
    @JsonIgnore
    var endsIn: OffsetDateTime = OffsetDateTime.now(Clock.systemUTC())
    @JsonIgnore
    var completed: Boolean = false

}