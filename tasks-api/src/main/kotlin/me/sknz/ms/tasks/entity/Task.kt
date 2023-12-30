package me.sknz.ms.tasks.entity

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Clock
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "ms_tasks")
class Task {

    @Id
    var id: UUID? = null
    var name: String = ""
    var description: String? = null

    @ManyToOne
    @JoinColumn(name = "author_id")
    var author: TaskAuthor? = null

    @JsonProperty("created_at")
    var createdAt: OffsetDateTime = OffsetDateTime.now(Clock.systemUTC())
    @JsonProperty("ends_in")
    var endsIn: OffsetDateTime = OffsetDateTime.now(Clock.systemUTC())
    var completed: Boolean = false

}