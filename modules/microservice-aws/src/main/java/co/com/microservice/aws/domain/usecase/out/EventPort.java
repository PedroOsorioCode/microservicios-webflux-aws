package co.com.microservice.aws.domain.usecase.out;

import co.com.microservice.aws.domain.model.events.Event;
import reactor.core.publisher.Mono;

public interface EventPort {
    Mono<Void> emitEvent(Event<Object> event, String messageId);
}