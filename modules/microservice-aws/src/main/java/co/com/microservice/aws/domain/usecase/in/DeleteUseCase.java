package co.com.microservice.aws.domain.usecase.in;

import co.com.microservice.aws.domain.model.rq.TransactionRequest;
import reactor.core.publisher.Mono;

public interface DeleteUseCase {
    Mono<String> delete(TransactionRequest request);
}