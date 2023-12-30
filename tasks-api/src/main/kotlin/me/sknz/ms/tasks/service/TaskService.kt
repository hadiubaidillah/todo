package me.sknz.ms.tasks.service

import me.sknz.ms.tasks.entity.Task
import me.sknz.ms.tasks.entity.TaskAuthor
import me.sknz.ms.tasks.model.TaskDTO
import me.sknz.ms.tasks.repository.*
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.OffsetDateTime
import java.util.*

@Service
class TaskService(
    private val tasks: TaskRepository,
    private val users: TaskAuthorRepository,
    private val rabbitmq: NotificationService
) {

    fun findAll(): List<Task> {
        return tasks.findByAuthorId(getUserId())
    }

    fun findById(id: UUID): Task {
        return tasks.findByIdAndAuthorId(id, getUserId()).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "The requested task was not found.")
        }
    }

    @Transactional
    fun create(taskDto: TaskDTO): Task {
        val task = Task()

        task.id = UUID.randomUUID()
        task.name = taskDto.name
        task.description = taskDto.description
        task.endsIn = taskDto.endsIn!!
        task.author = getUser()
        task.completed = taskDto.completed ?: false

        return tasks.save(rabbitmq.createTaskNotification(task))
    }

    @Transactional
    fun updateTask(id: UUID, dto: TaskDTO) {
        val task = this.findById(id)
        if (task.endsIn.isBefore(OffsetDateTime.now()) || task.completed) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "It is not possible to update a task that has already been completed.")
        }

        task.name = dto.name
        task.description = dto.description
        task.endsIn = dto.endsIn!!
        task.completed = dto.completed ?: false
        tasks.save(rabbitmq.createTaskNotification(task))
    }

    @Transactional
    fun deleteTask(id: UUID) {
        tasks.delete(rabbitmq.deleteTaskNotification(this.findById(id)))
    }

    fun getUserId(): UUID {
        return (SecurityContextHolder.getContext().authentication.principal as Jwt).let {
            UUID.fromString(it.getClaimAsString("sub"))
        }
    }

    fun getUser(): TaskAuthor {
        val jwt = SecurityContextHolder.getContext().authentication.principal as Jwt
        val id = getUserId()

        return users.findById(id).orElseGet {
            val author = TaskAuthor()
            author.id = id
            author.username = jwt.getClaimAsString("preferred_username")
            author.firstName = jwt.getClaimAsString("given_name")
            author.lastName = jwt.getClaimAsString("family_name")
            author.email = jwt.getClaimAsString("email")
            author.verified = jwt.getClaimAsBoolean("email_verified") ?: false

            return@orElseGet users.save(author)
        }
    }
}