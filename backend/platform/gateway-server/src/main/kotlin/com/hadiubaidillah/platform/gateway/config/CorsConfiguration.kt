package com.hadiubaidillah.platform.gateway.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

@Configuration
class CorsConfiguration {

    @Value("\${cors.allowed-origins}")
    private lateinit var allowedOrigins: String

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration().apply {
            allowedOrigins = this@CorsConfiguration.allowedOrigins.split(",")
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
            allowedHeaders = listOf("*")
            allowCredentials = true
            maxAge = 3600L
        }

        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", config)
        }
    }
}
