package com.hadiubaidillah.platform.gateway.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
class SecurityConfiguration {

    @Bean
    fun securityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .cors {}
            .csrf { it.disable() }
            .authorizeExchange { exchanges ->
                exchanges
                    .pathMatchers(
                        "/actuator/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/webjars/**",
                        "/fallback/**"
                    ).permitAll()
                    .anyExchange().authenticated()
            }
            .oauth2ResourceServer { it.jwt {} }
            .build()
    }
}
