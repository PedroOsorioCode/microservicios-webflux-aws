package co.com.microservice.aws.domain.model.gateway;

import reactor.core.publisher.Mono;

public interface FindById<T> {
    Mono<T> findById(T t);
}
