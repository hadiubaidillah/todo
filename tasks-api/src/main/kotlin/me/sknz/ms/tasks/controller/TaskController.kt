package me.sknz.ms.tasks.controller

import jakarta.validation.Valid
import me.sknz.ms.tasks.entity.Task
import me.sknz.ms.tasks.model.TaskDTO
import me.sknz.ms.tasks.service.TaskService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/tasks")
class TaskController(val service: TaskService) {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getTasks(): List<Task> {
        return service.findAll()
    }

    @GetMapping("/{id}")
    fun getTask(@PathVariable("id") uuid: UUID): Task {
        return service.findById(uuid)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createTask(@Valid @RequestBody dto: TaskDTO): Task {
        return service.create(dto)
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateTask(@PathVariable("id") id: UUID, @Valid @RequestBody dto: TaskDTO) {
        service.updateTask(id, dto)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTask(@PathVariable("id") id: UUID) {
        service.deleteTask(id)
    }
}