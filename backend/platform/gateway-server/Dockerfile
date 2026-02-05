FROM gradle:8.12-jdk25 AS builder
WORKDIR /app
COPY . .
RUN gradle :platform:gateway-server:bootJar --no-daemon

FROM eclipse-temurin:25-jre-alpine
COPY --from=builder /app/platform/gateway-server/build/libs/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
