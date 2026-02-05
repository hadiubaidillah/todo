package com.hadiubaidillah.platform.gateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class GatewayServerApplication

fun main(args: Array<String>) {
    runApplication<GatewayServerApplication>(*args)
}
