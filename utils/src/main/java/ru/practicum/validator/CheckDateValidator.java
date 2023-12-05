package ru.practicum.validator;

import lombok.ToString;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;
import java.time.LocalDateTime;

@SupportedValidationTarget(ValidationTarget.PARAMETERS)
@ToString
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
            return true;
        }
        return start.isBefore(end);
    }
}