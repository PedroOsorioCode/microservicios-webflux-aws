package co.com.microservice.aws.domain.usecase.out;

import reactor.core.publisher.Mono;

public interface CountByStatusPort {
    Mono<Integer> countByStatus(boolean status);
}