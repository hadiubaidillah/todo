package me.sknz.ms.tasks.notification.repository;

import me.sknz.ms.tasks.notification.entity.TaskNotification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface TaskNotificationRepository : JpaRepository<TaskNotification, UUID> {

    @Query("select t from TaskNotification t where t.task.author.id = ?1")
    fun findByAuthorId(id: UUID): List<TaskNotification>
    @Query("select t from TaskNotification t where t.readed = false and t.task.author.id = ?1")
    fun findUnreadsByAuthorId(id: UUID): List<TaskNotification>
    @Query("select t from TaskNotification t where t.id = ?1 and t.task.author.id = ?2")
    fun findByIdAndAuthorId(id: UUID, authorId: UUID): Optional<TaskNotification>
    @Query("select t from TaskNotification t where t.task.id = ?1 and t.type = ?2")
    fun findByTaskIdAndType(id: UUID, type: TaskNotification.NotificationType): Optional<TaskNotification>
    @Query("select (count(t) > 0) from TaskNotification t where t.task.id = ?1 and t.type = ?2")
    fun existsByTaskIdAndType(id: UUID, type: TaskNotification.NotificationType): Boolean

}