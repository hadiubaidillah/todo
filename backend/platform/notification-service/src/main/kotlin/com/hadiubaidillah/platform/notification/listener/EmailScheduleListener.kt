package com.hadiubaidillah.platform.notification.listener

import com.hadiubaidillah.platform.notification.model.EmailScheduleEvent
import com.hadiubaidillah.platform.notification.service.NotificationService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class EmailScheduleListener(
    private val notificationService: NotificationService
) : BaseMessageListener<EmailScheduleEvent> {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun onMessage(event: EmailScheduleEvent) {
        log.info("Delivering scheduled email to {} for {}:{}", event.email, event.sourceService, event.sourceId)
        notificationService.processEmailDelivery(event)
    }
}
