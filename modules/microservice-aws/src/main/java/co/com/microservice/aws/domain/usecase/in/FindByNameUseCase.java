package co.com.microservice.aws.domain.usecase.in;

import co.com.microservice.aws.domain.model.rq.TransactionRequest;
import co.com.microservice.aws.domain.model.rs.TransactionResponse;
import reactor.core.publisher.Mono;

public interface FindByNameUseCase {
    Mono<TransactionResponse> findByName(TransactionRequest request);
}