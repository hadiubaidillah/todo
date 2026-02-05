package com.hadiubaidillah.service.todo.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import java.time.OffsetDateTime

class EndsDateLimitValidator : ConstraintValidator<EndsDateLimit, OffsetDateTime?> {
    override fun isValid(value: OffsetDateTime?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) return true
        return value.isAfter(OffsetDateTime.now())
    }
}
