package com.hadiubaidillah.service.todo.scheduler

import com.hadiubaidillah.service.todo.repository.TaskRepository
import com.hadiubaidillah.service.todo.service.TaskNotificationPublisher
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Component
class OverdueTaskScheduler(
    private val taskRepository: TaskRepository,
    private val notificationPublisher: TaskNotificationPublisher
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(fixedRate = 60000) // Run every minute
    @Transactional
    fun checkOverdueTasks() {
        log.debug("Checking for overdue tasks...")

        val now = OffsetDateTime.now()
        val overdueTasks = taskRepository.findTasksNeedingOverdueNotification(now)

        if (overdueTasks.isNotEmpty()) {
            log.info("Found {} tasks that became overdue", overdueTasks.size)
        }

        for (task in overdueTasks) {
            try {
                val author = task.author
                if (author != null) {
                    notificationPublisher.taskBecameOverdue(task, author)

                    // Mark task as notified
                    val updated = task.copy(overdueNotified = true)
                    taskRepository.save(updated)

                    log.info("Sent overdue notification for task: {}", task.id)
                }
            } catch (e: Exception) {
                log.error("Failed to process overdue notification for task: {}", task.id, e)
            }
        }
    }
}
