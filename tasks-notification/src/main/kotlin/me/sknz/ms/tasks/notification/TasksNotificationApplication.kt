package me.sknz.ms.tasks.notification

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableTransactionManagement
@EnableDiscoveryClient
class TasksNotificationApplication

fun main(args: Array<String>) {
	runApplication<TasksNotificationApplication>(*args)
}