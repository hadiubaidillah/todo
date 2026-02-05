package com.hadiubaidillah.platform.notification.repository

import com.hadiubaidillah.platform.notification.entity.NotificationUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface NotificationUserRepository : JpaRepository<NotificationUser, UUID>
