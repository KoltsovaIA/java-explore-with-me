package ru.practicum.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class CheckDateValidator implements ConstraintValidator<StartBeforeEndDateValid, Object[]> {
    @Override
    public void initialize(StartBeforeEndDateValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object[] values,
                           ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = (LocalDateTime) values[0];
        LocalDateTime end = (LocalDateTime) values[1];
        if (start == null || end == null) {
            return false;
        }
        return start.isBefore(end);
    }
}