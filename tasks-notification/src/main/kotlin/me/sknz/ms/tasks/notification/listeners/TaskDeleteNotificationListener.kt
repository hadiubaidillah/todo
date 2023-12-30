package me.sknz.ms.tasks.notification.listeners

import me.sknz.ms.tasks.notification.configuration.RabbitMQConfiguration
import me.sknz.ms.tasks.notification.entity.Task
import me.sknz.ms.tasks.notification.entity.TaskNotification
import me.sknz.ms.tasks.notification.entity.TaskNotification.NotificationType
import me.sknz.ms.tasks.notification.model.TaskModel
import me.sknz.ms.tasks.notification.repository.TaskAuthorRepository
import me.sknz.ms.tasks.notification.repository.TaskNotificationRepository
import me.sknz.ms.tasks.notification.repository.TaskRepository
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component
import java.util.function.Consumer

/**
 * ## TaskDeleteNotificationListener
 *
 * Class responsible for receiving messages from the RabbitMQ queue
 * and create notifications informing task deletions.
 */
@Component
class TaskDeleteNotificationListener(
    private val template: RabbitTemplate,
    private val credentials: RabbitMQConfiguration,
    private val notifications: TaskNotificationRepository,
    private val tasks: TaskRepository,
    private val users: TaskAuthorRepository
) : JsonMessageListener() {

    /**
     * This function will receive a [TaskModel] to look for [Task],
     * create a notification informing that a task has been deleted,
     * and cancel task completion schedules.
     */
    override fun onMessage(message: Message) {
        val model = message.parse<TaskModel>()
        val task = tasks.findById(model.id).orElseGet {
            model.toTask(users.getAuthor(model.author)).let(tasks::save)
        }

        notifications.createSentNotification(task, NotificationType.DELETION)
        notifications.findByTaskIdAndType(model.id, NotificationType.CREATION).ifPresent {
            cancelSchedule(it)
        }
    }

    /**
     * This function will cancel a task scheduled through a queue in RabbitMQ
     */
    fun cancelSchedule(notification: TaskNotification) {
        val properties = MessageProperties().apply {
            messageId = notification.id.toString()
            headers["x-cancel-scheduled-message"] = true
        }

        template.send(credentials.exchange, credentials.getEmailNotificationRoutingKey(), Message(byteArrayOf(), properties))
    }
}