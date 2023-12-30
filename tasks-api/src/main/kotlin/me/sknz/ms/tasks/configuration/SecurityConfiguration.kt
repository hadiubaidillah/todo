package me.sknz.ms.tasks.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfiguration {

    @Bean
    fun security(http: HttpSecurity): SecurityFilterChain {
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
        val authoritiesConverter = JwtGrantedAuthoritiesConverter()
        val converter = JwtAuthenticationConverter()

        authoritiesConverter.setAuthoritiesClaimName("authorities")
        authoritiesConverter.setAuthorityPrefix("")
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter)

        return converter
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