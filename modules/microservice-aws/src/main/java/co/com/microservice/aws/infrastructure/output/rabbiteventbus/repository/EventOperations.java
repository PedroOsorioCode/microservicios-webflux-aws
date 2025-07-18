package co.com.microservice.aws.infrastructure.output.rabbiteventbus.repository;

import co.com.microservice.aws.application.helpers.logs.LoggerBuilder;
import co.com.microservice.aws.application.helpers.logs.TransactionLog;
import co.com.microservice.aws.domain.model.events.Event;
import lombok.RequiredArgsConstructor;
import org.reactivecommons.api.domain.DomainEvent;
import org.reactivecommons.api.domain.DomainEventBus;
import org.reactivecommons.async.impl.config.annotations.EnableDomainEventBus;
import reactor.core.publisher.Mono;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import static reactor.core.publisher.Mono.from;

@EnableDomainEventBus
@RequiredArgsConstructor
public class EventOperations {
    private static final String NAME_CLASS = EventOperations.class.getName();
    private static final String SPEC_VERSION = "1";
    private static final String APPLICATION_NAME = "microservice-aws";
    private static final String MSG_EVENT_EMITTED = "Event emitted";
    private final DomainEventBus domainEventBus;
    private final LoggerBuilder logger;

    public Mono<Void> emitEvent(Event<?> event, String messageId) {
        return generateDomainEvent(event).flatMap(domainEvent -> from(domainEventBus.emit(domainEvent)))
            .doOnSuccess(e ->
                logger.info(TransactionLog.Request.builder().body(event).build(), MSG_EVENT_EMITTED,
                        messageId, "generateDomainEvent", NAME_CLASS))
            .onErrorResume(this::printErroEmit);
    }

    private Mono<DomainEvent<?>> generateDomainEvent(Event<?> incompleteEvent) {
        return Mono.just(APPLICATION_NAME)
                .map(app -> incompleteEvent.complete(app, SPEC_VERSION, APPLICATION_JSON_VALUE))
                .map(event -> new DomainEvent<>(event.getType(), event.getId(), event));
    }

    private Mono<Void> printErroEmit(Throwable throwable) {
        logger.error(throwable);
        return Mono.empty();
    }
}