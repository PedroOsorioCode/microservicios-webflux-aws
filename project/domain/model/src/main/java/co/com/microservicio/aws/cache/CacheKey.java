package co.com.microservicio.aws.cache;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheKey {
    AUDIT_ON_SAVE("AUDIT_ON_SAVE"),
    AUDIT_ON_UPDATE("AUDIT_ON_UPDATE");

    private final String key;
}
