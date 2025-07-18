package co.com.microservice.aws.infrastructure.output.rabbiteventbus;

import co.com.microservice.aws.domain.model.events.Event;
import co.com.microservice.aws.domain.usecase.out.EventPort;
import co.com.microservice.aws.infrastructure.output.rabbiteventbus.repository.EventOperations;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ReactiveEventAdapter implements EventPort {
    private final EventOperations eventOperations;

    @Override
    public Mono<Void> emitEvent(Event<Object> event, String messageId) {
        return Mono.just(event).flatMap(e -> eventOperations.emitEvent(e, messageId));
    }
}