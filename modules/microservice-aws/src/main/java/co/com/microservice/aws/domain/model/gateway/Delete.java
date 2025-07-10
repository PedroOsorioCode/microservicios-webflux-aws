package co.com.microservice.aws.domain.model.gateway;

import reactor.core.publisher.Mono;

public interface Delete<T> {
    Mono<Void> delete(T t);
}
