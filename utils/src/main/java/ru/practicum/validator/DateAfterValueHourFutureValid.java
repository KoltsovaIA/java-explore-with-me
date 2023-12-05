package ru.practicum.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = DateAfterValueHourFutureValidator.class)
public @interface DateAfterValueHourFutureValid {
    String value();

    String message() default "Start must be after {value} hour future or not null";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
