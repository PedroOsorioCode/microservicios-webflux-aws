package co.com.microservicio.aws.asynceventbus;

import co.com.microservicio.aws.event.gateway.EventGateway;
import co.com.microservicio.aws.log.LoggerBuilder;
import co.com.microservicio.aws.model.worldregion.rq.TransactionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ReactiveEventsAdapter implements EventGateway {
    private final LoggerBuilder logger;

    @Override
    public Mono<Void> emitEvent(TransactionRequest request) {
        logger.info(request.getContext().getCustomer().getUsername(),
                request.getContext().getId(), "audit on list", "emitEvent");
        return Mono.empty();
    }
}
