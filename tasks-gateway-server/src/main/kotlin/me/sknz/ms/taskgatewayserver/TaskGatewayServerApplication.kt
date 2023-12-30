package me.sknz.ms.taskgatewayserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class TaskGatewayServerApplication

fun main(args: Array<String>) {
	runApplication<TaskGatewayServerApplication>(*args)
}
