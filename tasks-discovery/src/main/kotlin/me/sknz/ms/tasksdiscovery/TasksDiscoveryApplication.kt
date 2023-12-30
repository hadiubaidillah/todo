package me.sknz.ms.tasksdiscovery

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@SpringBootApplication
@EnableEurekaServer
class TasksdiscoveryApplication

fun main(args: Array<String>) {
	runApplication<TasksdiscoveryApplication>(*args)
}
