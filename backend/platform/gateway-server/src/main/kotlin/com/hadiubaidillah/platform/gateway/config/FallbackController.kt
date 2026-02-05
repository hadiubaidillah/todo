package com.hadiubaidillah.platform.gateway.config

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/fallback")
class FallbackController {

    @GetMapping("/todo")
    fun todoFallback(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(mapOf("message" to "Todo Service is currently unavailable. Please try again later."))
    }

    @GetMapping("/notification")
    fun notificationFallback(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(mapOf("message" to "Notification Service is currently unavailable. Please try again later."))
    }
}
