package me.sknz.ms.tasks.service

import me.sknz.ms.tasks.configuration.RabbitMQConfiguration
import me.sknz.ms.tasks.entity.Task
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.stereotype.Service

@Service
class NotificationService(private val template: AmqpTemplate, private val credentials: RabbitMQConfiguration) {

    fun createTaskNotification(task: Task): Task {
        return template.convertAndSend(credentials.exchange, credentials.getNewTaskRoutingKey(), task).let { task }
    }

    fun deleteTaskNotification(task: Task): Task {
        return template.convertAndSend(credentials.exchange, credentials.getDeleteNotificationRoutingKey(), task).let { task }
    }
}