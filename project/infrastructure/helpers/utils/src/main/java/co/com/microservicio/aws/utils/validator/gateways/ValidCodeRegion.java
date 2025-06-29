package co.com.microservicio.aws.utils.validator.gateways;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import co.com.microservicio.aws.utils.validator.CodeRegionValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = CodeRegionValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCodeRegion {
    String message() default "Code region must contain only letters, with 5 to 15 characters before the hyphen and 2 to 5 after. " +
            "No numbers or special characters are allowed";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}