package me.sknz.ms.tasks.notification.configuration

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.springframework.amqp.core.*
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties("application.rabbitmq")
@Configuration
class RabbitMQConfiguration {

    @NotEmpty
    lateinit var exchange: String
    @NotNull
    var queues: Queues? = null

    class Queues {
        @NotNull
        var tasks: QueueConfig? = null
        @NotNull
        var emailNotification: QueueConfig? = null
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
    fun directExchange(): DirectExchange = ExchangeBuilder.directExchange(exchange)
        .delayed()
        .build()

    @Bean
    fun newTaskQueue(): Queue = QueueBuilder.durable(queues!!.tasks!!.queue!!)
        .singleActiveConsumer()
        .build()

    @Bean
    fun emailNotificationQueue(): Queue = QueueBuilder.durable(queues!!.emailNotification!!.queue!!)
        .singleActiveConsumer()
        .build()

    @Bean
    fun deleteNotificationQueue(): Queue = QueueBuilder.durable(queues!!.deleteNotification!!.queue)
        .singleActiveConsumer()
        .build()

    @Bean
    fun newTaskBinding(): Binding = BindingBuilder.bind(newTaskQueue())
        .to(directExchange())
        .with(getNewTaskRoutingKey())

    @Bean
    fun emailNotificationBinding(): Binding = BindingBuilder.bind(emailNotificationQueue())
        .to(directExchange())
        .with(getEmailNotificationRoutingKey())

    @Bean
    fun deleteNotificationBinding(): Binding = BindingBuilder.bind(deleteNotificationQueue())
        .to(directExchange())
        .with(getDeleteNotificationRoutingKey())

    fun getNewTaskRoutingKey(): String = queues!!.tasks!!.routingKey!!
    fun getEmailNotificationRoutingKey(): String = queues!!.emailNotification!!.routingKey!!
    fun getDeleteNotificationRoutingKey(): String =  queues!!.deleteNotification!!.routingKey!!
}