package com.hadiubaidillah.platform.notification.model

import java.time.OffsetDateTime
import java.util.UUID

data class NotificationEvent(
    val userId: UUID,
    val sourceService: String,
    val sourceId: String,
    val type: String,
    val title: String,
    val message: String,
    val scheduleEmail: Boolean = false,
    val emailDeliveryAt: OffsetDateTime? = null,
    val userEmail: String? = null,
    val userName: String? = null
)
