package co.com.microservice.aws.domain.model.commons.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheKey {
    APPLY_AUDIT("APPLY_AUDIT"),
    KEY_DEFAULT("KEY_DEFAULT");

    private final String key;
}