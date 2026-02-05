dependencies {
    implementation(libs.spring.cloud.starter.gateway)
    implementation(libs.spring.cloud.starter.netflix.eureka.client)
    implementation(libs.spring.cloud.starter.circuitbreaker.reactor.resilience4j)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.oauth2.resource.server)
    implementation(libs.spring.boot.starter.data.redis.reactive)
    implementation(libs.micrometer.registry.prometheus)
    implementation(libs.micrometer.tracing.bridge.otel)
    implementation(libs.opentelemetry.exporter.zipkin)
    implementation(libs.springdoc.openapi.starter.webflux.ui)
}
