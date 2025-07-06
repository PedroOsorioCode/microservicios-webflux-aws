package co.com.microservicio.aws.event.gateway;

import co.com.microservicio.aws.model.worldregion.rq.TransactionRequest;
import reactor.core.publisher.Mono;

public interface EventGateway {
    Mono<Void> emitEvent(TransactionRequest request, String message);
}
