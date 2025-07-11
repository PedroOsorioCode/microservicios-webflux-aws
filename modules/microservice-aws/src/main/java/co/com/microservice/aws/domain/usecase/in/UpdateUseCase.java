package co.com.microservice.aws.domain.usecase.in;

import reactor.core.publisher.Mono;

public interface UpdateUseCase<T> {
    Mono<T> update(T t);
}