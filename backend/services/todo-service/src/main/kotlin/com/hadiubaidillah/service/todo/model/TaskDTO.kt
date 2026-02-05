package com.hadiubaidillah.service.todo.model

import com.hadiubaidillah.service.todo.validation.EndsDateLimit
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.OffsetDateTime

data class TaskDTO(
    @field:NotBlank(message = "Task name is required")
    @field:Size(min = 1, max = 255, message = "Task name must be between 1 and 255 characters")
    val name: String,

    @field:Size(max = 2000, message = "Description must be at most 2000 characters")
    val description: String? = null,

    @field:EndsDateLimit
    val endsIn: OffsetDateTime? = null
)
