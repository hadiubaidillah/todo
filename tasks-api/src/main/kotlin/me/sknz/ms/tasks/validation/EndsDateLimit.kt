package me.sknz.ms.tasks.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
@Constraint(validatedBy = [EndsDateLimitValidator::class])
annotation class EndsDateLimit(
    val message: String = "The end date must be a maximum of 2 weeks from the current date.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)