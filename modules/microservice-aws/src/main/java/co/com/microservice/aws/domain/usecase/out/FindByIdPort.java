package co.com.microservice.aws.domain.usecase.out;

import reactor.core.publisher.Mono;

public interface FindByIdPort<T> {
    Mono<T> findById(T t);
}