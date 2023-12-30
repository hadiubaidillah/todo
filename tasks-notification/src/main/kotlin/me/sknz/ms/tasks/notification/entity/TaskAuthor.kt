package me.sknz.ms.tasks.notification.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "ms_notifications_author")
class TaskAuthor {

    @Id
    var id: UUID? = null
    @JsonProperty("first_name")
    var firstName: String = ""
    @JsonProperty("last_name")
    var lastName: String = ""
    var username: String = ""
    @JsonIgnore
    var email: String? = ""

}