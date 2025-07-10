package co.com.microservice.aws.domain.model.gateway;

import reactor.core.publisher.Mono;

public interface Update<T> {
    Mono<T> update(T t);
}