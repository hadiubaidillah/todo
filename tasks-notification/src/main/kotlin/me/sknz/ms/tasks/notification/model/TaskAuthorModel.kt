package me.sknz.ms.tasks.notification.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Id
import me.sknz.ms.tasks.notification.entity.TaskAuthor
import java.util.*

class TaskAuthorModel {

    lateinit var id: UUID
    lateinit var name: String
    @JsonProperty("first_name")
    lateinit var firstName: String
    @JsonProperty("last_name")
    lateinit var lastName: String
    lateinit var username: String
    var email: String? = null
    var verified: Boolean = false

    fun toTaskAuthor(): TaskAuthor {
        val author = TaskAuthor()
        author.id = this.id
        author.firstName = this.firstName
        author.lastName = this.lastName
        author.email = this.email
        author.username = this.username

        return author
    }
}
