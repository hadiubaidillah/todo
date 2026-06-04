FROM gradle:jdk25 AS builder
WORKDIR /app
COPY . .
RUN gradle :services:ai-service:bootJar --no-daemon

FROM eclipse-temurin:25-jre-alpine
COPY --from=builder /app/services/ai-service/build/libs/*.jar /app/app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
