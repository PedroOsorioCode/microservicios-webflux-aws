package co.com.microservice.aws.domain.model.gateway;

import reactor.core.publisher.Flux;

public interface ListAll<T> {
    Flux<T> listAll();
}
