package me.sknz.ms.tasks

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableTransactionManagement
@EnableDiscoveryClient
class TasksApplication

fun main(args: Array<String>) {
	runApplication<TasksApplication>(*args)
}
