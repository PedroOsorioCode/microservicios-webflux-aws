package co.com.microservicio.aws.redis.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "adapters.cache.redis")
public class CacheProperties {
    private Integer expireTime;
}
