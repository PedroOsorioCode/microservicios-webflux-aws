package co.com.microservicio.aws.redis;

import co.com.microservicio.aws.cache.gateway.CacheGateway;
import co.com.microservicio.aws.redis.properties.CacheProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;

@Configuration
public class BooleanCacheConfig {

    @Bean
    public CacheGateway<Boolean> booleanCacheAdapter(
            ReactiveRedisConnectionFactory factory,
            CacheProperties cacheProperties){
        return new CacheManagementAdapter<>(Boolean.class, factory, cacheProperties);
    }
}
