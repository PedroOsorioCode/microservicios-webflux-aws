package co.com.microservice.aws.domain.model.gateway;

import reactor.core.publisher.Mono;

public interface Save<T> {
    Mono<T> save(T t);
}
