package com.hadiubaidillah.platform.notification.config

import com.hadiubaidillah.platform.notification.listener.EmailScheduleListener
import com.hadiubaidillah.platform.notification.listener.NotificationCancelListener
import com.hadiubaidillah.platform.notification.listener.NotificationEventListener
import com.hadiubaidillah.platform.notification.model.NotificationCancelEvent
import com.hadiubaidillah.platform.notification.model.NotificationEvent
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQListeners {

    @Value("\${rabbitmq.notification-events.queue}")
    private lateinit var notificationEventsQueue: String

    @Value("\${rabbitmq.notification-email.queue}")
    private lateinit var notificationEmailQueue: String

    @Value("\${rabbitmq.notification-cancel.queue}")
    private lateinit var notificationCancelQueue: String

    /**
     * Creates a Jackson converter that maps LinkedHashMap type headers
     * (sent by external services using mapOf()) to the correct target class.
     */
    private fun typedConverter(targetType: Class<*>): Jackson2JsonMessageConverter {
        val converter = Jackson2JsonMessageConverter()
        val typeMapper = DefaultJackson2JavaTypeMapper()
        typeMapper.setIdClassMapping(mapOf(
            "java.util.LinkedHashMap" to targetType
        ))
        converter.javaTypeMapper = typeMapper
        return converter
    }

    @Bean
    fun notificationEventsListenerContainer(
        connectionFactory: ConnectionFactory,
        notificationEventListener: NotificationEventListener
    ): SimpleMessageListenerContainer {
        val container = SimpleMessageListenerContainer(connectionFactory)
        container.setQueueNames(notificationEventsQueue)
        val adapter = MessageListenerAdapter(notificationEventListener, "onMessage")
        adapter.setMessageConverter(typedConverter(NotificationEvent::class.java))
        container.setMessageListener(adapter)
        return container
    }

    @Bean
    fun emailScheduleListenerContainer(
        connectionFactory: ConnectionFactory,
        emailScheduleListener: EmailScheduleListener,
        messageConverter: MessageConverter
    ): SimpleMessageListenerContainer {
        val container = SimpleMessageListenerContainer(connectionFactory)
        container.setQueueNames(notificationEmailQueue)
        val adapter = MessageListenerAdapter(emailScheduleListener, "onMessage")
        adapter.setMessageConverter(messageConverter)
        container.setMessageListener(adapter)
        return container
    }

    @Bean
    fun notificationCancelListenerContainer(
        connectionFactory: ConnectionFactory,
        notificationCancelListener: NotificationCancelListener
    ): SimpleMessageListenerContainer {
        val container = SimpleMessageListenerContainer(connectionFactory)
        container.setQueueNames(notificationCancelQueue)
        val adapter = MessageListenerAdapter(notificationCancelListener, "onMessage")
        adapter.setMessageConverter(typedConverter(NotificationCancelEvent::class.java))
        container.setMessageListener(adapter)
        return container
    }
}
