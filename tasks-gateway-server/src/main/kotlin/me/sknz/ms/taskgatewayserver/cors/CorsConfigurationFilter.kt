package me.sknz.ms.taskgatewayserver.cors

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

@Configuration
class CorsConfigurationFilter {

    @Bean
    fun corsFilter(): CorsWebFilter {
        val configuration = CorsConfiguration()

        configuration.allowedMethods = listOf("*")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true
        configuration.allowedOriginPatterns = listOf("*")
        configuration.maxAge = 3600L

        return CorsWebFilter(UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", configuration)
        })
    }
}