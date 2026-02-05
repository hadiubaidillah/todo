package com.hadiubaidillah.service.todo.controller

import com.hadiubaidillah.service.todo.entity.Task
import com.hadiubaidillah.service.todo.model.TaskDTO
import com.hadiubaidillah.service.todo.service.TaskService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "Tasks", description = "Task management API")
class TaskController(
    private val taskService: TaskService
) {

    @Operation(summary = "Get all tasks for current user")
    @GetMapping
    fun getTasks(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<List<Task>> {
        val userId = UUID.fromString(jwt.subject)
        return ResponseEntity.ok(taskService.getTasksByUser(userId))
    }

    @Operation(summary = "Get a specific task")
    @GetMapping("/{id}")
    fun getTask(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable id: UUID
    ): ResponseEntity<Task> {
        val task = taskService.getTask(id) ?: return ResponseEntity.notFound().build()
        if (task.author?.id != UUID.fromString(jwt.subject)) {
            return ResponseEntity.notFound().build()
        }
        return ResponseEntity.ok(task)
    }

    @Operation(summary = "Create a new task")
    @PostMapping
    fun createTask(
        @AuthenticationPrincipal jwt: Jwt,
        @Valid @RequestBody dto: TaskDTO
    ): ResponseEntity<Task> {
        val task = taskService.createTask(jwt, dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(task)
    }

    @Operation(summary = "Update an existing task")
    @PutMapping("/{id}")
    fun updateTask(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable id: UUID,
        @Valid @RequestBody dto: TaskDTO
    ): ResponseEntity<Task> {
        val task = taskService.updateTask(jwt, id, dto)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(task)
    }

    @Operation(summary = "Toggle task completion status")
    @PatchMapping("/{id}/toggle")
    fun toggleComplete(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable id: UUID
    ): ResponseEntity<Task> {
        val task = taskService.toggleComplete(jwt, id)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(task)
    }

    @Operation(summary = "Delete a task")
    @DeleteMapping("/{id}")
    fun deleteTask(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable id: UUID
    ): ResponseEntity<Unit> {
        return if (taskService.deleteTask(jwt, id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
