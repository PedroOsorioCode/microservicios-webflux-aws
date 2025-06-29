package co.com.microservicio.aws.utils.validator.gateways;

import co.com.microservicio.aws.utils.validator.NameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NameValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidName {
    String message() default "Name must contain only letters and must not exceed 50 characters. " +
        "Numbers and special characters are not allowed.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}