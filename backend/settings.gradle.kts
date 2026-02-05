rootProject.name = "microservices-platform"

include(
    "platform:discovery-server",
    "platform:gateway-server",
    "platform:notification-service",
    "services:todo-service"
)
