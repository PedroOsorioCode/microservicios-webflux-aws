package co.com.microservice.aws.domain.usecase.out;

import co.com.microservice.aws.domain.model.rq.Context;
import reactor.core.publisher.Mono;

public interface WorldCountryPort {
    Mono<Boolean> exist(Context context, String name);
}