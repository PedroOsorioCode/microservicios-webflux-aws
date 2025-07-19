package co.com.microservice.aws.infrastructure.input.listenevent.events;

import co.com.microservice.aws.application.helpers.logs.LoggerBuilder;
import co.com.microservice.aws.application.helpers.logs.TransactionLog;
import co.com.microservice.aws.domain.model.Country;
import co.com.microservice.aws.domain.model.events.SaveCountry;
import co.com.microservice.aws.domain.model.rq.Context;
import co.com.microservice.aws.domain.model.rq.TransactionRequest;
import co.com.microservice.aws.domain.usecase.in.CountryUseCase;
import co.com.microservice.aws.infrastructure.input.listenevent.config.EventNameProperties;
import co.com.microservice.aws.infrastructure.input.listenevent.util.EventData;
import lombok.RequiredArgsConstructor;
import org.reactivecommons.api.domain.DomainEvent;
import org.reactivecommons.async.api.HandlerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
public class CountryEventListener {
    private static final String NAME_CLASS = CountryEventListener.class.getName();

    private final EventNameProperties eventNameProperties;
    private final CountryUseCase countryUseCase;
    private final LoggerBuilder logger;

    @Bean
    @Primary
    public HandlerRegistry handlerRegistry() {
        logger.info(eventNameProperties.getSaveCountry());
        return HandlerRegistry.register()
            .listenEvent(eventNameProperties.getSaveCountry(), this::saveCountry, Object.class)
            .listenEvent(eventNameProperties.getSaveCacheCountCountry(), this::saveCacheCountCountryByStatus,
                Object.class);
    }

    private Mono<Void> saveCountry(DomainEvent<Object> event) {
        var saveCountry = EventData.getValueData(event, SaveCountry.class);
        var saveCountryData = saveCountry.getData();
        var headers = saveCountryData.getTransactionRequest().getHeaders();
        var request = TransactionRequest.builder()
            .item(buildCountry(saveCountryData.getTransactionResponse().getCountry()))
            .context(Context.builder()
                .customer(Context.Customer.builder().username(headers.getUsername()).build()).build())
            .build();

        printEventData(event, headers.getMessageId());
        return Mono.just(request).flatMap(countryUseCase::save)
                .onErrorResume(this::printFailed).then();
    }

    private Mono<Void> saveCacheCountCountryByStatus(DomainEvent<Object> event) {
        var saveCountry = EventData.getValueData(event, SaveCountry.class);
        var saveCountryData = saveCountry.getData();
        var headers = saveCountryData.getTransactionRequest().getHeaders();
        var request = TransactionRequest.builder()
                .item(buildCountry(saveCountryData.getTransactionResponse().getCountry())).build();
        var status = saveCountryData.getTransactionResponse().getCountry().isStatus();

        printEventData(event, headers.getMessageId());
        return Mono.just(request).flatMap(countryUseCase::countByStatus)
                .doOnNext(count -> logger.info(String.format("country status: %s, count: %s", status, count)))
                .doOnError(this::printFailed).then();
    }

    private Country buildCountry(SaveCountry.Country country){
        return Country.builder()
                .shortCode(country.getShortCode())
                .name(country.getName())
                .description(country.getDescription())
                .status(country.isStatus())
                .build();
    }

    private void printEventData(DomainEvent<?> event, String messageId) {
        logger.info(TransactionLog.Request.builder().body(event).build(),
                "Event save country", messageId, "Save Country", NAME_CLASS);
    }

    private Mono<String> printFailed(Throwable throwable) {
        logger.error(throwable);
        return Mono.empty();
    }
}