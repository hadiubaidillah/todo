package com.hadiubaidillah.service.ai.model

import jakarta.validation.constraints.NotBlank

data class ParseRequest(
    @field:NotBlank(message = "Text is required")
    val text: String,

    @field:NotBlank(message = "Timezone offset is required")
    val timezoneOffset: String
)
