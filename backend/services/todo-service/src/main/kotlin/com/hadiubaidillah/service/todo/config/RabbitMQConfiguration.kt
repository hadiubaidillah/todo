package com.hadiubaidillah.service.todo.config

import org.springframework.amqp.core.CustomExchange
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQConfiguration {

    @Value("\${rabbitmq.exchange}")
    private lateinit var exchange: String

    @Bean
    fun platformNotificationsExchange(): CustomExchange {
        val args = mapOf("x-delayed-type" to "direct")
        return CustomExchange(exchange, "x-delayed-message", true, false, args)
    }

    @Bean
    fun jsonMessageConverter(): MessageConverter = Jackson2JsonMessageConverter()
}
