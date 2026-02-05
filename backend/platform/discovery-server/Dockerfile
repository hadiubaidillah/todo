FROM gradle:8.12-jdk25 AS builder
WORKDIR /app
COPY . .
RUN gradle :platform:discovery-server:bootJar --no-daemon

FROM eclipse-temurin:25-jre-alpine
COPY --from=builder /app/platform/discovery-server/build/libs/*.jar /app/app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
