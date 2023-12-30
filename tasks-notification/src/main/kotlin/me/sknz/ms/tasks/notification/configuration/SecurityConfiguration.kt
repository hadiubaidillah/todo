package me.sknz.ms.tasks.notification.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfiguration {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.cors{ cors -> cors.allowAll() }
            .authorizeHttpRequests{
                authorizeHttpRequests -> authorizeHttpRequests
                .anyRequest()
                .authenticated()
            }.
            oauth2ResourceServer{
                oauth2ResourceServer -> oauth2ResourceServer
                .jwt { jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()) }
            }

        return http.build()
    }

    fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
        return JwtAuthenticationConverter().apply {
            val authoritiesConverter = JwtGrantedAuthoritiesConverter()
            authoritiesConverter.setAuthorityPrefix("")
            authoritiesConverter.setAuthoritiesClaimName("authorities")

            setJwtGrantedAuthoritiesConverter(authoritiesConverter)
        }
    }

    fun CorsConfigurer<HttpSecurity>.allowAll(): CorsConfigurer<HttpSecurity> {
        val configuration = CorsConfiguration()

        configuration.allowedMethods = listOf("*")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true
        configuration.allowedOriginPatterns = listOf("*")
        configuration.maxAge = 3600L

        return this.configurationSource(UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", configuration)
        })
    }
}