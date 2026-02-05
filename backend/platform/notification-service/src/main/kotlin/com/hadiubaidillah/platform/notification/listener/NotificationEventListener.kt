package com.hadiubaidillah.platform.notification.listener

import com.hadiubaidillah.platform.notification.model.NotificationEvent
import com.hadiubaidillah.platform.notification.service.NotificationService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class NotificationEventListener(
    private val notificationService: NotificationService
) : BaseMessageListener<NotificationEvent> {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun onMessage(event: NotificationEvent) {
        log.info("Received notification event: {} from {}", event.type, event.sourceService)
        notificationService.processNotificationEvent(event)
    }
}
