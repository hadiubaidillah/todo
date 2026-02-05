package com.hadiubaidillah.platform.notification.listener

import com.hadiubaidillah.platform.notification.model.NotificationCancelEvent
import com.hadiubaidillah.platform.notification.service.NotificationService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class NotificationCancelListener(
    private val notificationService: NotificationService
) : BaseMessageListener<NotificationCancelEvent> {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun onMessage(event: NotificationCancelEvent) {
        log.info("Cancelling notifications for {}:{}", event.sourceService, event.sourceId)
        notificationService.cancelNotifications(event.sourceService, event.sourceId)
    }
}
