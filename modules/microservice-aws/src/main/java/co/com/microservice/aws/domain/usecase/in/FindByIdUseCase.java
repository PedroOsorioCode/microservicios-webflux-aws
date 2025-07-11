package co.com.microservice.aws.domain.usecase.in;

import reactor.core.publisher.Mono;

public interface FindByIdUseCase<T> {
    Mono<T> findById(T t);
}