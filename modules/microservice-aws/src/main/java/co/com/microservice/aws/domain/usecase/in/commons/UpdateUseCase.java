package co.com.microservice.aws.domain.usecase.in.commons;

import co.com.microservice.aws.domain.model.rq.TransactionRequest;
import reactor.core.publisher.Mono;

public interface UpdateUseCase {
    Mono<String> update(TransactionRequest request);
}