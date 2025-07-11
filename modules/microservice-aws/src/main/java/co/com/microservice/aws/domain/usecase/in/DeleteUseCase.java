package co.com.microservice.aws.domain.usecase.in;

import reactor.core.publisher.Mono;

public interface DeleteUseCase<T> {
    Mono<Void> delete(T t);
}