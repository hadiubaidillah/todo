package com.hadiubaidillah.platform.notification.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "notifications")
data class Notification(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val userId: UUID,

    @Column(nullable = false)
    val sourceService: String,

    @Column(nullable = false)
    val sourceId: String,

    @Column(nullable = false)
    val type: String,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    val message: String,

    @Column(nullable = false)
    val read: Boolean = false,

    @Column(nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now()
)
