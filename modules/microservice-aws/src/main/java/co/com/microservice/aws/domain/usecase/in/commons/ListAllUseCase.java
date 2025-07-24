package co.com.microservice.aws.domain.usecase.in.commons;

import co.com.microservice.aws.domain.model.rq.TransactionRequest;
import co.com.microservice.aws.domain.model.rs.TransactionResponse;
import reactor.core.publisher.Mono;

public interface ListAllUseCase {
    Mono<TransactionResponse> listAll(TransactionRequest request);
}