package me.sknz.ms.tasks.notification.listeners

import me.sknz.ms.tasks.notification.entity.Task
import me.sknz.ms.tasks.notification.entity.TaskAuthor
import me.sknz.ms.tasks.notification.entity.TaskNotification
import me.sknz.ms.tasks.notification.model.TaskAuthorModel
import me.sknz.ms.tasks.notification.repository.TaskAuthorRepository
import me.sknz.ms.tasks.notification.repository.TaskNotificationRepository
import org.springframework.amqp.rabbit.core.RabbitTemplate
import java.util.*
import java.util.function.Consumer

fun TaskAuthorRepository.getAuthor(model: TaskAuthorModel): TaskAuthor {
    val user = findById(model.id).orElse(null)
        ?: return save(model.toTaskAuthor())

    var equals = true
    if (user.email != model.email) {
        user.email = model.email
        equals = false
    }

    if (user.username != model.username) {
        user.username = model.username
        equals = false
    }

    if ("${user.firstName} ${user.lastName}" != model.name) {
        user.firstName = model.firstName
        user.lastName = model.lastName
        equals = false
    }

    return if (!equals) save(user) else user
}

fun TaskNotificationRepository.createSentNotification(task: Task, type: TaskNotification.NotificationType) {
    if (existsByTaskIdAndType(task.id!!, type)) {
        return
    }

    val finished = TaskNotification()
    finished.id = UUID.randomUUID()
    finished.task = task
    finished.type = type
    finished.sent = true
    save(finished)
}