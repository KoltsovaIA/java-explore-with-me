package ru.practicum.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumAllowedValidator implements ConstraintValidator<EnumAllowedValid, Enum<?>> {
    private Set<String> allowedValues;
    private Set<String> enumValues;

    @Override
    public void initialize(EnumAllowedValid constraint) {
        this.allowedValues = Arrays.stream(constraint.allowed()).collect(Collectors.toSet());
        this.enumValues = Stream.of(constraint.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
        return enumValues.contains(value.toString()) && allowedValues.contains(value.toString());
    }
}
