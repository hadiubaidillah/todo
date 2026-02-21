package com.hadiubaidillah.service.ai.controller

import com.hadiubaidillah.service.ai.model.ParseRequest
import com.hadiubaidillah.service.ai.model.ParsedTask
import com.hadiubaidillah.service.ai.service.TaskParserService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/ai")
class AiController(
    private val taskParserService: TaskParserService
) {

    @PostMapping("/tasks/parse")
    fun parseTasks(@Valid @RequestBody request: ParseRequest): ResponseEntity<List<ParsedTask>> {
        val tasks = taskParserService.parseTasks(request)
        return ResponseEntity.ok(tasks)
    }
}
