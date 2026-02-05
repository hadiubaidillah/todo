package com.hadiubaidillah.service.todo.service

import com.hadiubaidillah.service.todo.entity.Task
import com.hadiubaidillah.service.todo.entity.TaskAuthor
import com.hadiubaidillah.service.todo.model.TaskDTO
import com.hadiubaidillah.service.todo.repository.TaskAuthorRepository
import com.hadiubaidillah.service.todo.repository.TaskRepository
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class TaskService(
    private val taskRepository: TaskRepository,
    private val taskAuthorRepository: TaskAuthorRepository,
    private val notificationPublisher: TaskNotificationPublisher
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Cacheable("tasks", key = "#userId")
    fun getTasksByUser(userId: UUID): List<Task> {
        return taskRepository.findByAuthorIdOrderByCreatedAtDesc(userId)
    }

    fun getTask(id: UUID): Task? {
        return taskRepository.findById(id).orElse(null)
    }

    @Transactional
    @CacheEvict("tasks", key = "#jwt.subject")
    fun createTask(jwt: Jwt, dto: TaskDTO): Task {
        val author = getOrCreateAuthor(jwt)
        val task = Task(
            name = dto.name,
            description = dto.description,
            endsIn = dto.endsIn,
            author = author
        )
        val saved = taskRepository.save(task)
        notificationPublisher.taskCreated(saved, author)
        return saved
    }

    @Transactional
    @CacheEvict("tasks", key = "#jwt.subject")
    fun updateTask(jwt: Jwt, id: UUID, dto: TaskDTO): Task? {
        log.info("updateTask called for id: {}", id)
        val userId = UUID.fromString(jwt.subject)
        val existing = taskRepository.findById(id).orElse(null) ?: return null
        // Eagerly fetch author to avoid lazy loading issues after save
        val authorProxy = existing.author ?: return null
        if (authorProxy.id != userId) return null
        // Force initialization of all author fields before save
        val author = taskAuthorRepository.findById(authorProxy.id).orElse(null) ?: return null
        log.info("updateTask: author loaded - id={}, email={}", author.id, author.email)

        val wasOverdue = existing.isOverdue()

        val updated = existing.copy(
            name = dto.name,
            description = dto.description,
            endsIn = dto.endsIn,
            // Reset overdueNotified if deadline is extended
            overdueNotified = if (wasOverdue && dto.endsIn != null && !existing.completed) false else existing.overdueNotified
        )
        val saved = taskRepository.save(updated)
        log.info("updateTask: task saved, calling notificationPublisher.taskUpdated")
        notificationPublisher.taskUpdated(saved, author)

        // Send notification when OVERDUE task is extended to IN_PROGRESS
        if (wasOverdue && saved.isInProgress()) {
            log.info("updateTask: task extended from overdue, sending extended notification")
            notificationPublisher.taskExtendedFromOverdue(saved, author)
        }

        log.info("updateTask: notificationPublisher.taskUpdated completed")
        return saved
    }

    @Transactional
    @CacheEvict("tasks", key = "#jwt.subject")
    fun toggleComplete(jwt: Jwt, id: UUID): Task? {
        val userId = UUID.fromString(jwt.subject)
        val existing = taskRepository.findById(id).orElse(null) ?: return null
        val authorProxy = existing.author ?: return null
        if (authorProxy.id != userId) return null

        val wasOverdue = existing.isOverdue()
        val wasInProgress = existing.isInProgress()
        val updated = existing.copy(completed = !existing.completed)
        val saved = taskRepository.save(updated)

        // Send notification when task is completed (from IN_PROGRESS or OVERDUE)
        if (saved.completed && (wasOverdue || wasInProgress)) {
            val author = taskAuthorRepository.findById(authorProxy.id).orElse(null)
            if (author != null) {
                notificationPublisher.taskCompleted(saved, author, wasOverdue)
            }
        }

        return saved
    }

    @Transactional
    @CacheEvict("tasks", key = "#jwt.subject")
    fun deleteTask(jwt: Jwt, id: UUID): Boolean {
        val userId = UUID.fromString(jwt.subject)
        val existing = taskRepository.findById(id).orElse(null) ?: return false
        // Save author and task name before delete to avoid lazy loading issues after entity is removed
        val author = existing.author ?: return false
        if (author.id != userId) return false
        val taskName = existing.name

        taskRepository.delete(existing)
        // Use saved references since existing entity may no longer be accessible after delete
        notificationPublisher.taskDeleted(existing.copy(name = taskName), author)
        return true
    }

    private fun getOrCreateAuthor(jwt: Jwt): TaskAuthor {
        val userId = UUID.fromString(jwt.subject)
        return taskAuthorRepository.findById(userId).orElseGet {
            taskAuthorRepository.save(
                TaskAuthor(
                    id = userId,
                    email = jwt.getClaimAsString("email") ?: "",
                    firstName = jwt.getClaimAsString("given_name"),
                    lastName = jwt.getClaimAsString("family_name")
                )
            )
        }
    }
}
