package com.hadiubaidillah.platform.notification.controller

import com.hadiubaidillah.platform.notification.entity.Notification
import com.hadiubaidillah.platform.notification.service.NotificationService
import com.hadiubaidillah.platform.notification.service.SseEmitterService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.UUID

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications", description = "Generic notification management API")
class NotificationController(
    private val notificationService: NotificationService,
    private val sseEmitterService: SseEmitterService
) {

    @Operation(summary = "SSE stream for realtime notifications")
    @GetMapping("/stream")
    fun stream(@AuthenticationPrincipal jwt: Jwt): SseEmitter {
        val userId = UUID.fromString(jwt.subject)
        return sseEmitterService.subscribe(userId)
    }

    @Operation(summary = "Get all notifications for current user")
    @GetMapping
    fun getNotifications(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestParam(required = false) source: String?
    ): ResponseEntity<List<Notification>> {
        val userId = UUID.fromString(jwt.subject)
        val notifications = if (source != null) {
            notificationService.getNotificationsBySource(userId, source)
        } else {
            notificationService.getNotifications(userId)
        }
        return ResponseEntity.ok(notifications)
    }

    @Operation(summary = "Get unread notifications for current user")
    @GetMapping("/unread")
    fun getUnreadNotifications(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<List<Notification>> {
        val userId = UUID.fromString(jwt.subject)
        return ResponseEntity.ok(notificationService.getUnreadNotifications(userId))
    }

    @Operation(summary = "Get unread notification count")
    @GetMapping("/unread/count")
    fun getUnreadCount(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<Map<String, Long>> {
        val userId = UUID.fromString(jwt.subject)
        return ResponseEntity.ok(mapOf("count" to notificationService.getUnreadCount(userId)))
    }

    @Operation(summary = "Get a specific notification")
    @GetMapping("/{id}")
    fun getNotification(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable id: UUID
    ): ResponseEntity<Notification> {
        val notification = notificationService.getNotification(id)
            ?: return ResponseEntity.notFound().build()
        if (notification.userId != UUID.fromString(jwt.subject)) {
            return ResponseEntity.notFound().build()
        }
        return ResponseEntity.ok(notification)
    }

    @Operation(summary = "Mark a notification as read")
    @PutMapping("/{id}/read")
    fun markAsRead(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable id: UUID
    ): ResponseEntity<Notification> {
        val userId = UUID.fromString(jwt.subject)
        val notification = notificationService.markAsRead(userId, id)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(notification)
    }

    @Operation(summary = "Mark all notifications as read")
    @PutMapping("/read-all")
    fun markAllAsRead(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<Unit> {
        val userId = UUID.fromString(jwt.subject)
        notificationService.markAllAsRead(userId)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "Delete a notification")
    @DeleteMapping("/{id}")
    fun deleteNotification(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable id: UUID
    ): ResponseEntity<Unit> {
        val userId = UUID.fromString(jwt.subject)
        return if (notificationService.deleteNotification(userId, id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
