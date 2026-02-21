package com.hadiubaidillah.service.ai.model

data class ParsedTask(
    val name: String,
    val description: String? = null,
    val endsIn: String? = null
)
