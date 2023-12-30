package me.sknz.ms.tasks.notification.listeners

import me.sknz.ms.tasks.notification.configuration.RabbitMQConfiguration
import me.sknz.ms.tasks.notification.entity.Task
import me.sknz.ms.tasks.notification.entity.TaskAuthor
import me.sknz.ms.tasks.notification.entity.TaskNotification
import me.sknz.ms.tasks.notification.entity.TaskNotification.NotificationType
import me.sknz.ms.tasks.notification.model.ScheduledNotification
import me.sknz.ms.tasks.notification.model.TaskModel
import me.sknz.ms.tasks.notification.repository.TaskAuthorRepository
import me.sknz.ms.tasks.notification.repository.TaskNotificationRepository
import me.sknz.ms.tasks.notification.repository.TaskRepository
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.*
import java.util.function.Consumer

/**
 * ## TasksMessageListener
 *
 * Class responsible for receiving messages from the RabbitMQ queue
 * and update tasks and schedule notifications in the email notification queue.
 *
 * @see EmailNotificationMessageListener
 */
@Component
class TasksMessageListener(
    private val template: RabbitTemplate,
    private val credentials: RabbitMQConfiguration,
    private val tasks: TaskRepository,
    private val notifications: TaskNotificationRepository,
    private val users: TaskAuthorRepository
) : JsonMessageListener() {

    /**
     * This function will receive a [TaskModel] to save in the database,
     * update if already exists and schedule an email notification.
     */
    @Transactional
    override fun onMessage(message: Message) {
        val model = message.parse<TaskModel>()
        val possibleTask = tasks.findById(model.id)
        if (possibleTask.isPresent) {
            updateTaskNotification(possibleTask.get(), model)
            return
        }

        val author: TaskAuthor = users.getAuthor(model.author)
        val task: Task = tasks.save(model.toTask(author))

        val notification = TaskNotification()
        notification.id = UUID.randomUUID()
        notification.type = NotificationType.CREATION
        notification.task = task

        scheduleNotification(notifications.save(notification))
    }

    /**
     * This function will update an existing task and reschedule a notification
     * if the end of the task is changed.
     */
    fun updateTaskNotification(task: Task, model: TaskModel) {
        val oldEndsIn = task.endsIn

        task.endsIn = model.endsIn
        task.completed = model.completed
        task.description = model.description
        task.createdAt = model.createdAt
        task.name = model.name

        tasks.save(task)
        if (task.endsIn == oldEndsIn) {
            return
        }

        if (task.endsIn.isBefore(OffsetDateTime.now())) {
            return
        }

        notifications.findByTaskIdAndType(task.id!!, NotificationType.CREATION).ifPresent {
            if (it.sent) return@ifPresent
            cancelSchedule(it, this::scheduleNotification)
        }
    }

    /**
     * This function will schedule a task through a queue in RabbitMQ
     */
    fun scheduleNotification(notification: TaskNotification) {
        val properties = MessageProperties().apply {
            messageId = notification.id.toString()
            delay = (Date.from(notification.task.endsIn.toInstant()).time - System.currentTimeMillis()).toInt()
        }

        val message = Message(mapper.writeValueAsBytes(ScheduledNotification(notification)), properties)
        template.send(credentials.exchange, credentials.getEmailNotificationRoutingKey(), message)
    }

    /**
     * This function will cancel a task scheduled through a queue in RabbitMQ
     */
    fun cancelSchedule(notification: TaskNotification, consumer: Consumer<TaskNotification>? = null) {
        val properties = MessageProperties().apply {
            messageId = notification.id.toString()
            headers["x-cancel-scheduled-message"] = true
        }

        if (consumer == null) {
            template.send(credentials.exchange, credentials.getEmailNotificationRoutingKey(), Message(byteArrayOf(), properties))
            return
        }

        template.sendAndReceive(credentials.exchange, credentials.getEmailNotificationRoutingKey(), Message(byteArrayOf(), properties)).let {
            consumer.accept(notification)
        }
    }
}