package com.hadiubaidillah.platform.notification.config

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.CustomExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQConfiguration {

    @Value("\${rabbitmq.exchange}")
    private lateinit var exchange: String

    @Value("\${rabbitmq.notification-events.queue}")
    private lateinit var notificationEventsQueue: String

    @Value("\${rabbitmq.notification-events.routing-key}")
    private lateinit var notificationEventsRoutingKey: String

    @Value("\${rabbitmq.notification-email.queue}")
    private lateinit var notificationEmailQueue: String

    @Value("\${rabbitmq.notification-email.routing-key}")
    private lateinit var notificationEmailRoutingKey: String

    @Value("\${rabbitmq.notification-cancel.queue}")
    private lateinit var notificationCancelQueue: String

    @Value("\${rabbitmq.notification-cancel.routing-key}")
    private lateinit var notificationCancelRoutingKey: String

    @Bean
    fun platformNotificationsExchange(): CustomExchange {
        val args = mapOf("x-delayed-type" to "direct")
        return CustomExchange(exchange, "x-delayed-message", true, false, args)
    }

    @Bean
    fun notificationEventsQueue(): Queue = Queue(notificationEventsQueue, true)

    @Bean
    fun notificationEmailQueue(): Queue = Queue(notificationEmailQueue, true)

    @Bean
    fun notificationCancelQueue(): Queue = Queue(notificationCancelQueue, true)

    @Bean
    fun notificationEventsBinding(): Binding =
        BindingBuilder.bind(notificationEventsQueue())
            .to(platformNotificationsExchange())
            .with(notificationEventsRoutingKey)
            .noargs()

    @Bean
    fun notificationEmailBinding(): Binding =
        BindingBuilder.bind(notificationEmailQueue())
            .to(platformNotificationsExchange())
            .with(notificationEmailRoutingKey)
            .noargs()

    @Bean
    fun notificationCancelBinding(): Binding =
        BindingBuilder.bind(notificationCancelQueue())
            .to(platformNotificationsExchange())
            .with(notificationCancelRoutingKey)
            .noargs()

    @Bean
    fun jsonMessageConverter(): MessageConverter = Jackson2JsonMessageConverter()
}
