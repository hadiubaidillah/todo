package me.sknz.ms.tasks.notification.service

import me.sknz.ms.tasks.notification.entity.TaskNotification
import me.sknz.ms.tasks.notification.repository.TaskAuthorRepository
import me.sknz.ms.tasks.notification.repository.TaskNotificationRepository
import me.sknz.ms.tasks.notification.repository.TaskRepository
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class NotificationService(private val notifications: TaskNotificationRepository) {

    fun findAllNotifications(): List<TaskNotification> {
        return notifications.findByAuthorId(getUserId())
    }

    fun findUnreadNotifications(): List<TaskNotification> {
        return notifications.findUnreadsByAuthorId(getUserId())
    }
    
    fun findNotification(id: UUID): TaskNotification {
        return notifications.findByIdAndAuthorId(id, getUserId()).orElseThrow { 
            ResponseStatusException(HttpStatus.NOT_FOUND, "The requested notification was not found.")
        }
    }

    fun deleteNotification(id: UUID) {
        notifications.delete(findNotification(id))
    }

    fun markAllAsRead() {
        notifications.saveAll(this.findUnreadNotifications().map { it.readed = true; it })
    }
    
    fun markAsRead(id: UUID) {
        val notification = this.findNotification(id)
        notification.readed = true
        notifications.save(notification)
    }
    
    fun getUserId(): UUID = UUID.fromString((SecurityContextHolder.getContext().authentication.principal as Jwt).subject)

}