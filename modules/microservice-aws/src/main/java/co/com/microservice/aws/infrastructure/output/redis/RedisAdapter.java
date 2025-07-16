package co.com.microservice.aws.infrastructure.output.redis;

import co.com.microservice.aws.application.helpers.logs.LoggerBuilder;
import co.com.microservice.aws.domain.usecase.out.RedisPort;
import co.com.microservice.aws.infrastructure.output.redis.repository.RedisCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RedisAdapter implements RedisPort {
    private final RedisCacheRepository redisRepository;
    private final LoggerBuilder logger;

    @Override
    public Mono<String> find(String key) {
        return redisRepository.find(key).doOnNext(logger::info);
    }

    @Override
    public Mono<Boolean> save(String key, String value) {
        return redisRepository.save(key, value);
    }

    @Override
    public Mono<Boolean> delete(String key) {
        return redisRepository.delete(key);
    }
}