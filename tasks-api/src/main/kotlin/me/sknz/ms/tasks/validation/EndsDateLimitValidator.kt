package me.sknz.ms.tasks.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class EndsDateLimitValidator: ConstraintValidator<EndsDateLimit, OffsetDateTime> {

    override fun isValid(value: OffsetDateTime, context: ConstraintValidatorContext): Boolean {
        if (value.toInstant().isBefore(OffsetDateTime.now().toInstant())) {
            return false
        } else if (value.toInstant().isAfter(OffsetDateTime.now().plusDays(14).toInstant())) {
            return false
        }
        return true
    }
}
