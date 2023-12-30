package me.sknz.ms.tasks.notification.listeners

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageListener

abstract class JsonMessageListener: MessageListener {
    
    companion object {
        val mapper: ObjectMapper = ObjectMapper().findAndRegisterModules()
    }

    inline fun <reified T> Message.parse() = mapper.readValue<T>(body)
}