package me.sknz.ms.tasks.entity

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "ms_tasks_user")
class TaskAuthor {

    @Id
    var id: UUID? = null
    @JsonProperty("first_name")
    var firstName: String = ""
    @JsonProperty("last_name")
    var lastName: String = ""
    var username: String = ""
    var email: String? = ""
    var verified: Boolean = false

    @JsonProperty("name")
    fun getFullName() = "$firstName $lastName"
}