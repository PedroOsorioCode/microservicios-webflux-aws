package co.com.microservice.aws.domain.usecase.out;

import co.com.microservice.aws.domain.model.rq.Context;
import reactor.core.publisher.Flux;

public interface ListAllPort<T> {
    Flux<T> listAll(Context context);
}