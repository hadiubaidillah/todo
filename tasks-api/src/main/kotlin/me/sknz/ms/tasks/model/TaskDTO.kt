package me.sknz.ms.tasks.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import me.sknz.ms.tasks.validation.EndsDateLimit
import org.springframework.validation.annotation.Validated
import java.time.OffsetDateTime

@Validated
class TaskDTO {

    @NotEmpty
    val name: String = ""
    val description: String? = null

    @NotNull
    @JsonProperty("ends_in")
    @EndsDateLimit
    val endsIn: OffsetDateTime? = OffsetDateTime.now()
    val completed: Boolean? = false
}
