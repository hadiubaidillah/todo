package com.hadiubaidillah.platform.notification.model

data class NotificationCancelEvent(
    val sourceService: String,
    val sourceId: String
)
