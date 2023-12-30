package me.sknz.ms.tasks.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.springframework.amqp.core.*
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties("application.rabbitmq")
@Configuration
class RabbitMQConfiguration(val mapper: ObjectMapper) {

    @NotEmpty
    lateinit var exchange: String
    @NotNull
    var queues: Queues? = null

    class Queues {
        @NotNull
        var tasks: QueueConfig? = null
        @NotNull
        var deleteNotification: QueueConfig? = null

        class QueueConfig {
            @NotEmpty
            var queue: String? = null
            @NotEmpty
            var routingKey: String? = null
        }
    }

    @Bean
    fun messageConverter(): MessageConverter = Jackson2JsonMessageConverter(mapper)

    @Bean
    fun directExchange(): DirectExchange = ExchangeBuilder.directExchange(exchange)
        .delayed()
        .build()

    @Bean
    fun tasksQueue(): Queue = QueueBuilder.durable(queues!!.tasks!!.queue)
        .singleActiveConsumer()
        .build()

    @Bean
    fun deleteNotificationQueue(): Queue = QueueBuilder.durable(queues!!.deleteNotification!!.queue)
        .singleActiveConsumer()
        .build()

    @Bean
    fun tasksBinding(): Binding = BindingBuilder.bind(tasksQueue())
        .to(directExchange())
        .with(getNewTaskRoutingKey())

    @Bean
    fun deleteNotificationBinding(): Binding = BindingBuilder.bind(deleteNotificationQueue())
        .to(directExchange())
        .with(getDeleteNotificationRoutingKey())

    fun getNewTaskRoutingKey(): String = queues!!.tasks!!.routingKey!!
    fun getDeleteNotificationRoutingKey(): String = queues!!.deleteNotification!!.routingKey!!
}