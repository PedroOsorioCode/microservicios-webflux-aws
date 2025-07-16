package co.com.microservice.aws.infrastructure.output.redis.repository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RedisCacheRepository {
    private final ReactiveStringRedisTemplate redisTemplate;

    @Value("${adapters.redis.expireTime}")
    private int durationDefault;

    public Mono<Boolean> save(String key, String value) {
        return redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(durationDefault));
    }

    public Mono<String> find(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public Mono<Boolean> delete(String key) {
        return redisTemplate.opsForValue().delete(key);
    }

    public Mono<Boolean> exists(String key) {
        return redisTemplate.hasKey(key);
    }
}