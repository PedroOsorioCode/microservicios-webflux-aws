package co.com.microservice.aws.domain.usecase.out;

import reactor.core.publisher.Mono;

public interface FindByNamePort<T> {
    Mono<T> findByName(T t);
}