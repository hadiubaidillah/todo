package me.sknz.ms.tasks.notification.controller

import me.sknz.ms.tasks.notification.entity.TaskNotification
import me.sknz.ms.tasks.notification.service.NotificationService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/notifications")
class NotificationController(private val service: NotificationService) {

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    fun getNotifications(): List<TaskNotification> {
        return service.findAllNotifications()
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getNotification(@PathVariable("id") uuid: UUID): TaskNotification {
        return service.findNotification(uuid)
    }

    @GetMapping("/unreads")
    @ResponseStatus(HttpStatus.OK)
    fun getUnreadNotifications(): List<TaskNotification> {
        return service.findUnreadNotifications()
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteNotification(@PathVariable("id") uuid: UUID) {
        service.deleteNotification(uuid)
    }

    @PutMapping("/unreads")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun markAllAsRead() {
        service.markAllAsRead()
    }

    @PutMapping("/unreads/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun markAsRead(@PathVariable("id") uuid: UUID) {
        service.markAsRead(uuid)
    }
}