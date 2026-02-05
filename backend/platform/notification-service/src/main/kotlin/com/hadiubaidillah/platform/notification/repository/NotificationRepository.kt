package com.hadiubaidillah.platform.notification.repository

import com.hadiubaidillah.platform.notification.entity.Notification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface NotificationRepository : JpaRepository<Notification, UUID> {
    fun findByUserIdOrderByCreatedAtDesc(userId: UUID): List<Notification>
    fun findByUserIdAndReadFalseOrderByCreatedAtDesc(userId: UUID): List<Notification>
    fun findByUserIdAndSourceServiceOrderByCreatedAtDesc(userId: UUID, sourceService: String): List<Notification>
    fun countByUserIdAndReadFalse(userId: UUID): Long
    fun deleteBySourceServiceAndSourceId(sourceService: String, sourceId: String)
}
