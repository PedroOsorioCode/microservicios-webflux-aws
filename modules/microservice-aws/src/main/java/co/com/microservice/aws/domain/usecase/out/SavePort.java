package co.com.microservice.aws.domain.usecase.out;

import co.com.microservice.aws.domain.model.rq.Context;
import reactor.core.publisher.Mono;

public interface SavePort<T> {
    Mono<T> save(T t, Context context);
}