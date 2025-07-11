package co.com.microservice.aws.domain.usecase.out;

import reactor.core.publisher.Mono;

public interface UpdatePort<T> {
    Mono<T> update(T t);
}