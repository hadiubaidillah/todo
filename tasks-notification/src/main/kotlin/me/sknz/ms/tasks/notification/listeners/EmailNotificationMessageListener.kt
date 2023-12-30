package me.sknz.ms.tasks.notification.listeners

import me.sknz.ms.tasks.notification.entity.TaskNotification.NotificationType
import me.sknz.ms.tasks.notification.model.ScheduledNotification
import me.sknz.ms.tasks.notification.repository.TaskNotificationRepository
import me.sknz.ms.tasks.notification.repository.TaskRepository
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.amqp.core.Message
import org.springframework.data.repository.findByIdOrNull
import org.springframework.mail.MailException
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

/**
 * ## EmailNotificationMessageListener
 * Class responsible for receiving messages from the RabbitMQ queue
 * and send notifications by Email.
 */
@Component
class EmailNotificationMessageListener(
    private val notifications: TaskNotificationRepository,
    private val tasks: TaskRepository,
    private val javamail: JavaMailSender
) : JsonMessageListener() {

    val logger: Logger = LogManager.getLogger(this::class.java)

    /**
     * This function will receive a scheduled notification [ScheduledNotification],
     * check if the user who sent it has an email address and send a message informing the
     * completion of a task.
     */
    override fun onMessage(message: Message) {
        if (message.messageProperties.headers.containsKey("x-cancel-scheduled-message")) {
            return
        }

        val scheduled = message.parse<ScheduledNotification>()
        val task = tasks.findByIdOrNull(scheduled.taskId) ?: return

        if (task.author.email.isNullOrEmpty()) {
            notifications.createSentNotification(task, NotificationType.FINISHED)
            return
        }

        try {
            javamail.send(SimpleMailMessage().apply {
                setTo(task.author.email)
                subject = "To-do: ${task.name}"
                text = "Your task ${task.name} (${task.description ?: "Without description"}) was finished!"
            })

            logger.debug("Notified user ${task.author.username} with email ${task.author.email}")
        } catch (e: MailException) {
            logger.error("An error occurred when trying to send an email", e)
        } finally {
            notifications.createSentNotification(task, NotificationType.FINISHED)
        }
    }
}