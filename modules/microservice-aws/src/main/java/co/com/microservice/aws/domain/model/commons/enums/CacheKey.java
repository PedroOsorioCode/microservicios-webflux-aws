package co.com.microservice.aws.domain.model.commons.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheKey {
    APPLY_AUDIT("APPLY_AUDIT");

    private final String key;
}