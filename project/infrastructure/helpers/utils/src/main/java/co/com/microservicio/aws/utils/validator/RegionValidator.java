package co.com.microservicio.aws.utils.validator;

import co.com.microservicio.aws.utils.validator.gateways.ValidRegion;
import org.springframework.beans.factory.annotation.Value;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RegionValidator implements ConstraintValidator<ValidRegion, String> {
    private String regionPattern;

    public RegionValidator(@Value("${entries.regex-body-wr.region}") String regionPattern) {
        this.regionPattern = regionPattern;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.matches(regionPattern);
    }
}
