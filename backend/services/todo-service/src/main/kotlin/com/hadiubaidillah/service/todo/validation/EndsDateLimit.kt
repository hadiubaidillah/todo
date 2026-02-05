package com.hadiubaidillah.service.todo.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [EndsDateLimitValidator::class])
annotation class EndsDateLimit(
    val message: String = "End date must be in the future",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
