package co.com.microservice.aws.domain.usecase.out;

import reactor.core.publisher.Mono;

public interface FindByShortCodePort<T> {
    Mono<T> findByShortCode(T t);
}