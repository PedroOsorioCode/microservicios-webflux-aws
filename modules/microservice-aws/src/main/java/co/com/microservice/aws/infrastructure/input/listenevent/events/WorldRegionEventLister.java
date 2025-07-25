package co.com.microservice.aws.infrastructure.input.listenevent.events;

import co.com.microservice.aws.application.helpers.logs.LoggerBuilder;
import co.com.microservice.aws.application.helpers.logs.TransactionLog;
import co.com.microservice.aws.domain.model.events.ProccessWorldRegionFile;
import co.com.microservice.aws.domain.model.rq.Context;
import co.com.microservice.aws.domain.model.rq.TransactionRequest;
import co.com.microservice.aws.domain.usecase.in.WorldRegionUseCase;
import co.com.microservice.aws.infrastructure.input.listenevent.config.EventNameProperties;
import co.com.microservice.aws.infrastructure.input.listenevent.util.EventData;
import lombok.RequiredArgsConstructor;
import org.reactivecommons.api.domain.DomainEvent;
import org.reactivecommons.async.api.HandlerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
public class WorldRegionEventLister {
    private static final String NAME_CLASS = WorldRegionEventLister.class.getName();

    private final EventNameProperties eventNameProperties;
    private final WorldRegionUseCase worldRegionUseCase;
    private final LoggerBuilder logger;

    @Bean
    public HandlerRegistry handlerRegistryWorldRegion() {
        logger.info(eventNameProperties.getSaveWorldRegion());
        return HandlerRegistry.register()
                .listenEvent(eventNameProperties.getSaveWorldRegion(), this::saveWorldRegion, Object.class);
    }

    private Mono<Void> saveWorldRegion(DomainEvent<Object> event) {
        var saveWorldRegion = EventData.getValueData(event, ProccessWorldRegionFile.class);
        var saveWorldRegionData = saveWorldRegion.getData();
        var headers = saveWorldRegionData.getTransactionRequest().getHeaders();
        var request = TransactionRequest.builder()
                .item(saveWorldRegionData.getTransactionResponse().getResponse())
                .context(Context.builder().id(headers.getMessageId()).build())
                .build();

        printEventData(event, headers.getMessageId());
        return Mono.just(request).flatMap(worldRegionUseCase::processFile)
                .onErrorResume(this::printFailed).then();
    }

    private void printEventData(DomainEvent<?> event, String messageId) {
        logger.info(TransactionLog.Request.builder().body(event).build(),
                "Event save WorldRegion", messageId, "Save WorldRegion", NAME_CLASS);
    }

    private Mono<String> printFailed(Throwable throwable) {
        logger.error(throwable);
        return Mono.empty();
    }
}