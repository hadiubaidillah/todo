plugins {
    alias(libs.plugins.kotlin.jpa)
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.data.redis)
    implementation(libs.spring.boot.starter.cache)
    implementation(libs.spring.boot.starter.amqp)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.oauth2.resource.server)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.cloud.starter.netflix.eureka.client)
    implementation(libs.spring.cloud.starter.circuitbreaker.resilience4j)
    implementation(libs.micrometer.registry.prometheus)
    implementation(libs.micrometer.tracing.bridge.otel)
    implementation(libs.opentelemetry.exporter.zipkin)
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
    runtimeOnly(libs.postgresql)

    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}
