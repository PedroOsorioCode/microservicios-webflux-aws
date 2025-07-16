package co.com.microservice.aws.domain.usecase.out;

import reactor.core.publisher.Mono;

public interface RedisPort {
    Mono<String> find(String key);
    Mono<Boolean> save(String key, String value);
    Mono<Boolean> delete(String key);
}