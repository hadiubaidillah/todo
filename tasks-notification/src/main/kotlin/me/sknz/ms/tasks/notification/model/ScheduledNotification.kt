package me.sknz.ms.tasks.notification.model

import com.fasterxml.jackson.annotation.JsonProperty
import me.sknz.ms.tasks.notification.entity.TaskNotification
import java.util.UUID

class ScheduledNotification() {

    constructor(notification: TaskNotification): this() {
        this.initialNotificationId = notification.id!!
        this.taskId = notification.task.id!!
    }

    @JsonProperty("notification_id")
    lateinit var initialNotificationId: UUID
    @JsonProperty("task_id")
    lateinit var taskId: UUID
}