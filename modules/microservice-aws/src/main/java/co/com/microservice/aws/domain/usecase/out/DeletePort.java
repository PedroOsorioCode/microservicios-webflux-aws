package co.com.microservice.aws.domain.usecase.out;

import reactor.core.publisher.Mono;

public interface DeletePort<T> {
    Mono<Void> delete(T t);
}