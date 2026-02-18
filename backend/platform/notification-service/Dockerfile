FROM gradle:jdk25 AS builder
WORKDIR /app
COPY . .
RUN gradle :platform:notification-service:bootJar --no-daemon

FROM eclipse-temurin:25-jre-alpine
COPY --from=builder /app/platform/notification-service/build/libs/*.jar /app/app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
