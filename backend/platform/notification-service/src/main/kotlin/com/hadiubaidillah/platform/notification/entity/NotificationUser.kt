package com.hadiubaidillah.platform.notification.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "notification_users")
data class NotificationUser(
    @Id
    val id: UUID,

    @Column(nullable = false)
    val email: String,

    val firstName: String? = null,

    val lastName: String? = null
)
