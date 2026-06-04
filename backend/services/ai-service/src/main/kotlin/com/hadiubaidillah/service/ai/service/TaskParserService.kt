package com.hadiubaidillah.service.ai.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.hadiubaidillah.service.ai.model.ParseRequest
import com.hadiubaidillah.service.ai.model.ParsedTask
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Service
class TaskParserService(
    private val objectMapper: ObjectMapper,
    @Value("\${ANTHROPIC_API_KEY}") private val apiKey: String
) {
    private val restClient = RestClient.builder()
        .baseUrl("https://api.anthropic.com")
        .build()

    fun parseTasks(request: ParseRequest): List<ParsedTask> {
        val systemPrompt = buildSystemPrompt(request.timezoneOffset)

        val requestBody = mapOf(
            "model" to "claude-haiku-4-5-20251001",
            "max_tokens" to 1024,
            "system" to systemPrompt,
            "messages" to listOf(
                mapOf("role" to "user", "content" to request.text)
            )
        )

        val responseJson = restClient.post()
            .uri("/v1/messages")
            .header("x-api-key", apiKey)
            .header("anthropic-version", "2023-06-01")
            .contentType(MediaType.APPLICATION_JSON)
            .body(requestBody)
            .retrieve()
            .body(String::class.java)
            ?: throw RuntimeException("Empty response from Claude API")

        val responseNode = objectMapper.readTree(responseJson)
        val text = responseNode["content"]?.firstOrNull()?.get("text")?.asText()
            ?: throw RuntimeException("Unexpected response structure from Claude API")

        val jsonText = text.trim().let {
            val startIdx = it.indexOf('[')
            val endIdx = it.lastIndexOf(']') + 1
            if (startIdx >= 0 && endIdx > startIdx) it.substring(startIdx, endIdx) else "[]"
        }

        return objectMapper.readValue(
            jsonText,
            objectMapper.typeFactory.constructCollectionType(List::class.java, ParsedTask::class.java)
        )
    }

    private fun buildSystemPrompt(timezoneOffset: String): String {
        val now = OffsetDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy HH:mm", Locale.ENGLISH)
        val formattedDate = now.format(formatter)

        return """
You are a task extraction assistant. Extract tasks from natural language input in any language (including Bahasa Indonesia).
Current date and time: $formattedDate UTC$timezoneOffset

Return ONLY a JSON array with no additional text, explanation, or markdown formatting:
[{"name":"task name","description":null,"endsIn":"2026-02-23T14:00:00$timezoneOffset"}]

Rules:
- Resolve relative dates (besok/tomorrow, next Monday, Jumat/Friday, dll) from the current date above
- If no time is mentioned for a deadline, default to 23:59:00
- Split into multiple tasks when clearly multiple distinct tasks are mentioned
- "endsIn" must be in ISO 8601 format with timezone offset (e.g. "2026-02-23T14:00:00+07:00")
- "endsIn" is null if no deadline is mentioned
- "description" is null unless there are additional details beyond the task name
- Task names should be concise but descriptive
- Return tasks in the order they were mentioned
        """.trimIndent()
    }
}
