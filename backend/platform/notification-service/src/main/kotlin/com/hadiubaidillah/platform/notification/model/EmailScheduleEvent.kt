package com.hadiubaidillah.platform.notification.model

import java.util.UUID

data class EmailScheduleEvent(
    val userId: UUID,
    val sourceService: String,
    val sourceId: String,
    val email: String,
    val userName: String?,
    val subject: String,
    val body: String,
    val emailId: UUID = UUID.randomUUID()
)
