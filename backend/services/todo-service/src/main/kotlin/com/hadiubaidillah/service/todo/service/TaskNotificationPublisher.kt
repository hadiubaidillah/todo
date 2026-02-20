package com.hadiubaidillah.service.todo.service

import com.hadiubaidillah.service.todo.entity.Task
import com.hadiubaidillah.service.todo.entity.TaskAuthor
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class TaskNotificationPublisher(
    private val rabbitTemplate: RabbitTemplate,
    private val circuitBreakerFactory: CircuitBreakerFactory<*, *>
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${rabbitmq.exchange}")
    private lateinit var exchange: String

    @Value("\${rabbitmq.notification-events.routing-key}")
    private lateinit var notificationEventsRoutingKey: String

    @Value("\${rabbitmq.notification-cancel.routing-key}")
    private lateinit var notificationCancelRoutingKey: String

    fun taskCreated(task: Task, author: TaskAuthor) {
        val cb = circuitBreakerFactory.create("notificationPublisher")
        cb.run({
            val event = mapOf(
                "userId" to author.id,
                "sourceService" to "todo-service",
                "sourceId" to task.id.toString(),
                "type" to "CREATION",
                "title" to "Task Created: ${task.name}",
                "message" to "You created task '${task.name}'${task.endsIn?.let { ", due $it" } ?: ""}",
                "scheduleEmail" to (task.endsIn != null),
                "emailDeliveryAt" to task.endsIn,
                "userEmail" to author.email,
                "userName" to "${author.firstName ?: ""} ${author.lastName ?: ""}".trim()
            )
            rabbitTemplate.convertAndSend(exchange, notificationEventsRoutingKey, event)
            log.info("Published task creation notification for task: {}", task.id)
        }, { throwable ->
            log.error("Failed to publish task creation notification for task: {}", task.id, throwable)
        })
    }

    fun taskUpdated(task: Task, author: TaskAuthor) {
        val cb = circuitBreakerFactory.create("notificationPublisher")
        cb.run({
            val event = mapOf(
                "userId" to author.id,
                "sourceService" to "todo-service",
                "sourceId" to task.id.toString(),
                "type" to "UPDATE",
                "title" to "Task Updated: ${task.name}",
                "message" to "You updated task '${task.name}'${task.endsIn?.let { ", due $it" } ?: ""}",
                "scheduleEmail" to (task.endsIn != null),
                "emailDeliveryAt" to task.endsIn,
                "userEmail" to author.email,
                "userName" to "${author.firstName ?: ""} ${author.lastName ?: ""}".trim()
            )
            rabbitTemplate.convertAndSend(exchange, notificationEventsRoutingKey, event)
            log.info("Published task update notification for task: {}", task.id)
        }, { throwable ->
            log.error("Failed to publish task update notification for task: {}", task.id, throwable)
        })
    }

    fun taskDeleted(task: Task, author: TaskAuthor) {
        val cb = circuitBreakerFactory.create("notificationPublisher")
        cb.run({
            val event = mapOf(
                "userId" to author.id,
                "sourceService" to "todo-service",
                "sourceId" to task.id.toString(),
                "type" to "DELETION",
                "title" to "Task Deleted: ${task.name}",
                "message" to "Task '${task.name}' has been deleted",
                "scheduleEmail" to true,
                "emailDeliveryAt" to OffsetDateTime.now(),
                "userEmail" to author.email,
                "userName" to "${author.firstName ?: ""} ${author.lastName ?: ""}".trim()
            )
            rabbitTemplate.convertAndSend(exchange, notificationEventsRoutingKey, event)
            log.info("Published task deletion notification for task: {}", task.id)
        }, { throwable ->
            log.error("Failed to publish task deletion notification for task: {}", task.id, throwable)
        })
    }

    fun taskBecameOverdue(task: Task, author: TaskAuthor) {
        val cb = circuitBreakerFactory.create("notificationPublisher")
        cb.run({
            val event = mapOf(
                "userId" to author.id,
                "sourceService" to "todo-service",
                "sourceId" to task.id.toString(),
                "type" to "OVERDUE",
                "title" to "Task Overdue: ${task.name}",
                "message" to "Task '${task.name}' has passed its deadline",
                "scheduleEmail" to true,
                "emailDeliveryAt" to OffsetDateTime.now(),
                "userEmail" to author.email,
                "userName" to "${author.firstName ?: ""} ${author.lastName ?: ""}".trim()
            )
            rabbitTemplate.convertAndSend(exchange, notificationEventsRoutingKey, event)
            log.info("Published task overdue notification for task: {}", task.id)
        }, { throwable ->
            log.error("Failed to publish task overdue notification for task: {}", task.id, throwable)
        })
    }

    fun taskCompleted(task: Task, author: TaskAuthor, wasOverdue: Boolean) {
        val cb = circuitBreakerFactory.create("notificationPublisher")
        cb.run({
            val message = if (wasOverdue) {
                "Task '${task.name}' has been completed (was overdue)"
            } else {
                "Task '${task.name}' has been completed"
            }
            val event = mapOf(
                "userId" to author.id,
                "sourceService" to "todo-service",
                "sourceId" to task.id.toString(),
                "type" to "COMPLETED",
                "title" to "Task Completed: ${task.name}",
                "message" to message,
                "scheduleEmail" to true,
                "emailDeliveryAt" to OffsetDateTime.now(),
                "userEmail" to author.email,
                "userName" to "${author.firstName ?: ""} ${author.lastName ?: ""}".trim()
            )
            rabbitTemplate.convertAndSend(exchange, notificationEventsRoutingKey, event)
            log.info("Published task completed notification for task: {}", task.id)
        }, { throwable ->
            log.error("Failed to publish task completed notification for task: {}", task.id, throwable)
        })
    }

    fun taskExtendedFromOverdue(task: Task, author: TaskAuthor) {
        val cb = circuitBreakerFactory.create("notificationPublisher")
        cb.run({
            val event = mapOf(
                "userId" to author.id,
                "sourceService" to "todo-service",
                "sourceId" to task.id.toString(),
                "type" to "EXTENDED",
                "title" to "Task Extended: ${task.name}",
                "message" to "Task '${task.name}' deadline has been extended${task.endsIn?.let { ", new deadline: $it" } ?: ""}",
                "scheduleEmail" to (task.endsIn != null),
                "emailDeliveryAt" to task.endsIn,
                "userEmail" to author.email,
                "userName" to "${author.firstName ?: ""} ${author.lastName ?: ""}".trim()
            )
            rabbitTemplate.convertAndSend(exchange, notificationEventsRoutingKey, event)
            log.info("Published task extended notification for task: {}", task.id)
        }, { throwable ->
            log.error("Failed to publish task extended notification for task: {}", task.id, throwable)
        })
    }
}
