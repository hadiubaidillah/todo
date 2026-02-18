FROM gradle:jdk25 AS builder
WORKDIR /app
COPY . .
RUN gradle :services:todo-service:bootJar --no-daemon

FROM eclipse-temurin:25-jre-alpine
COPY --from=builder /app/services/todo-service/build/libs/*.jar /app/app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
