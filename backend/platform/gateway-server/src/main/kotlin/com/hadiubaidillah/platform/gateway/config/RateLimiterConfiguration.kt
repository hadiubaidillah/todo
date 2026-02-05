package com.hadiubaidillah.platform.gateway.config

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Mono

@Configuration
class RateLimiterConfiguration {

    @Bean
    fun userKeyResolver(): KeyResolver {
        return KeyResolver { exchange ->
            val principal = exchange.request.headers.getFirst("Authorization")
            Mono.just(principal ?: exchange.request.remoteAddress?.address?.hostAddress ?: "anonymous")
        }
    }
}
