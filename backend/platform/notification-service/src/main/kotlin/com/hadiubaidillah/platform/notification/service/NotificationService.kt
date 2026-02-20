package com.hadiubaidillah.platform.notification.service

import com.hadiubaidillah.platform.notification.entity.Notification
import com.hadiubaidillah.platform.notification.entity.NotificationUser
import com.hadiubaidillah.platform.notification.model.EmailScheduleEvent
import com.hadiubaidillah.platform.notification.model.NotificationEvent
import com.hadiubaidillah.platform.notification.repository.NotificationRepository
import com.hadiubaidillah.platform.notification.repository.NotificationUserRepository
import jakarta.mail.internet.MimeMessage
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.MessagePostProcessor
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val notificationUserRepository: NotificationUserRepository,
    private val rabbitTemplate: RabbitTemplate,
    private val mailSender: JavaMailSender,
    private val sseEmitterService: SseEmitterService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${rabbitmq.exchange}")
    private lateinit var exchange: String

    @Value("\${rabbitmq.notification-email.routing-key}")
    private lateinit var emailRoutingKey: String

    @Transactional
    @CacheEvict(value = ["notificationCount"], key = "#event.userId")
    fun processNotificationEvent(event: NotificationEvent) {
        val notification = Notification(
            userId = event.userId,
            sourceService = event.sourceService,
            sourceId = event.sourceId,
            type = event.type,
            title = event.title,
            message = event.message
        )
        val saved = notificationRepository.save(notification)
        sseEmitterService.broadcast(event.userId, saved)

        ensureUserExists(event.userId, event.userEmail, event.userName)

        if (event.scheduleEmail && event.emailDeliveryAt != null && event.userEmail != null) {
            scheduleEmail(event)
        }

        log.info("Processed notification event: {} from {}", event.type, event.sourceService)
    }

    fun scheduleEmail(event: NotificationEvent) {
        val delayMillis = ChronoUnit.MILLIS.between(OffsetDateTime.now(), event.emailDeliveryAt)
        if (delayMillis <= 0) {
            sendEmail(event.userEmail!!, event.userName, event.title, event.message)
            return
        }

        val emailEvent = EmailScheduleEvent(
            userId = event.userId,
            sourceService = event.sourceService,
            sourceId = event.sourceId,
            email = event.userEmail!!,
            userName = event.userName,
            subject = event.title,
            body = event.message
        )

        val postProcessor = MessagePostProcessor { message ->
            message.messageProperties.setHeader("x-delay", delayMillis)
            message
        }

        rabbitTemplate.convertAndSend(exchange, emailRoutingKey, emailEvent, postProcessor)
        log.info("Scheduled email for {} with delay {}ms", event.userEmail, delayMillis)
    }

    fun processEmailDelivery(event: EmailScheduleEvent) {
        sendEmail(event.email, event.userName, event.subject, event.body)
    }

    @Transactional
    fun cancelNotifications(sourceService: String, sourceId: String) {
        notificationRepository.deleteBySourceServiceAndSourceId(sourceService, sourceId)
        log.info("Cancelled notifications for {}:{}", sourceService, sourceId)
    }

    fun getNotifications(userId: UUID): List<Notification> {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
    }

    fun getUnreadNotifications(userId: UUID): List<Notification> {
        return notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId)
    }

    fun getNotificationsBySource(userId: UUID, source: String): List<Notification> {
        return notificationRepository.findByUserIdAndSourceServiceOrderByCreatedAtDesc(userId, source)
    }

    fun getNotification(id: UUID): Notification? {
        return notificationRepository.findById(id).orElse(null)
    }

    @Transactional
    @CacheEvict(value = ["notificationCount"], key = "#userId")
    fun markAsRead(userId: UUID, id: UUID): Notification? {
        val notification = notificationRepository.findById(id).orElse(null) ?: return null
        if (notification.userId != userId) return null
        val updated = notification.copy(read = true)
        return notificationRepository.save(updated)
    }

    @Transactional
    @CacheEvict(value = ["notificationCount"], key = "#userId")
    fun markAllAsRead(userId: UUID) {
        val unread = notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId)
        val updated = unread.map { it.copy(read = true) }
        notificationRepository.saveAll(updated)
    }

    @Transactional
    @CacheEvict(value = ["notificationCount"], key = "#userId")
    fun deleteNotification(userId: UUID, id: UUID): Boolean {
        val notification = notificationRepository.findById(id).orElse(null) ?: return false
        if (notification.userId != userId) return false
        notificationRepository.delete(notification)
        return true
    }

    @Cacheable(value = ["notificationCount"], key = "#userId")
    fun getUnreadCount(userId: UUID): Long {
        return notificationRepository.countByUserIdAndReadFalse(userId)
    }

    private fun ensureUserExists(userId: UUID, email: String?, name: String?) {
        if (email == null) return
        if (!notificationUserRepository.existsById(userId)) {
            val parts = name?.split(" ", limit = 2)
            notificationUserRepository.save(
                NotificationUser(
                    id = userId,
                    email = email,
                    firstName = parts?.getOrNull(0),
                    lastName = parts?.getOrNull(1)
                )
            )
        }
    }

    private fun sendEmail(to: String, name: String?, subject: String, body: String) {
        try {
            val message: MimeMessage = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true)
            helper.setTo(to)
            helper.setSubject(subject)
            helper.setText(
                """
                <html>
                <body>
                    <p>Hello ${name ?: "User"},</p>
                    <p>$body</p>
                    <br/>
                    <p>— Microservices Platform</p>
                </body>
                </html>
                """.trimIndent(),
                true
            )
            mailSender.send(message)
            log.info("Email sent to {}: {}", to, subject)
        } catch (e: Exception) {
            log.error("Failed to send email to {}: {}", to, e.message, e)
        }
    }
}
