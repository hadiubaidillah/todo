package com.hadiubaidillah.platform.notification

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
@EnableCaching
class NotificationServiceApplication

fun main(args: Array<String>) {
    runApplication<NotificationServiceApplication>(*args)
}
