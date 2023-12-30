package me.sknz.ms.tasks.notification.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import me.sknz.ms.tasks.notification.listeners.TasksMessageListener
import me.sknz.ms.tasks.notification.listeners.EmailNotificationMessageListener
import me.sknz.ms.tasks.notification.listeners.TaskDeleteNotificationListener
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQListeners(val connection: ConnectionFactory,
                        val credentials: RabbitMQConfiguration) {

    @Bean
    fun newTaskListener(listener: TasksMessageListener): SimpleMessageListenerContainer {
        return SimpleMessageListenerContainer(connection).apply                          {
            setQueueNames(credentials.queues!!.tasks!!.queue!!)
            setMessageListener(listener)
        }
    }

    @Bean
    fun emailNotificationListener(listener: EmailNotificationMessageListener): SimpleMessageListenerContainer {
        return SimpleMessageListenerContainer(connection).apply {
            setQueueNames(credentials.queues!!.emailNotification!!.queue!!)
            setMessageListener(listener)
        }
    }

    @Bean
    fun deleteNotificationListener(listener: TaskDeleteNotificationListener): SimpleMessageListenerContainer {
        return SimpleMessageListenerContainer(connection).apply {
            setQueueNames(credentials.queues!!.deleteNotification!!.queue!!)
            setMessageListener(listener)
        }
    }

    @Bean
    fun getMessageConverter(mapper: ObjectMapper): MessageConverter = Jackson2JsonMessageConverter(mapper)
}
