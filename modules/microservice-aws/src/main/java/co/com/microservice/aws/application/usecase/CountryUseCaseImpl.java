package co.com.microservice.aws.application.usecase;

import co.com.microservice.aws.application.helpers.commons.UseCase;
import co.com.microservice.aws.domain.model.Country;
import co.com.microservice.aws.domain.model.commons.enums.CacheKey;
import co.com.microservice.aws.domain.model.commons.exception.BusinessException;
import co.com.microservice.aws.domain.model.commons.exception.TechnicalException;
import co.com.microservice.aws.domain.model.commons.util.ResponseMessageConstant;
import co.com.microservice.aws.domain.model.rq.Context;
import co.com.microservice.aws.domain.model.rq.TransactionRequest;
import co.com.microservice.aws.domain.model.rs.TransactionResponse;
import co.com.microservice.aws.domain.usecase.in.*;
import co.com.microservice.aws.domain.usecase.out.*;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static co.com.microservice.aws.domain.model.commons.enums.BusinessExceptionMessage.BUSINESS_RECORD_NOT_FOUND;
import static co.com.microservice.aws.domain.model.commons.enums.BusinessExceptionMessage.BUSINESS_USERNAME_REQUIRED;
import static co.com.microservice.aws.domain.model.commons.enums.TechnicalExceptionMessage.TECHNICAL_REQUEST_ERROR;
import static co.com.microservice.aws.domain.model.events.EventType.EVENT_EMMITED_NOTIFICATION_SAVE;

@UseCase
@RequiredArgsConstructor
public class CountryUseCaseImpl implements CountryUseCase {
    private final SavePort<Country> countrySaver;
    private final ListAllPort<Country> countryLister;
    private final UpdatePort<Country> countryUpdater;
    private final DeletePort<Country> countryDeleter;
    private final FindByShortCodePort<Country> countryFinder;
    private final RedisPort redisPort;
    private final SentEventUseCase eventUseCase;
    private final CountByStatusPort countryCounter;

    @Override
    public Mono<TransactionResponse> listAll(TransactionRequest request) {
        return Mono.just(request)
            .filter(this::userIsRequired)
            .flatMap(req -> redisPort.find(CacheKey.APPLY_AUDIT.getKey()).thenReturn(req))
            .flatMap(req -> countryLister.listAll(req.getContext()).collectList().flatMap(this::buildResponse)
            ).switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(BUSINESS_USERNAME_REQUIRED))));
    }

    @Override
    public Mono<String> save(TransactionRequest request) {
        return Mono.just(request)
            .filter(this::userIsRequired)
            .map(TransactionRequest::getItem)
            .flatMap(this::buildCountry)
            .flatMap(country -> countrySaver.save(country, request.getContext()))
            .doOnNext(country -> eventUseCase.sentEvent(request.getContext(),
                    EVENT_EMMITED_NOTIFICATION_SAVE, Country.builder().name(country.getName())
                            .description(country.getDescription()).shortCode(country.getShortCode())
                            .status(country.isStatus()).build()))
            .thenReturn(ResponseMessageConstant.MSG_SAVED_SUCCESS);
    }

    @Override
    public Mono<String> delete(TransactionRequest request) {
        return Mono.just(request)
                .filter(this::userIsRequired)
                .map(rq -> Country.builder().id(Long.valueOf(rq.getParams().get("id"))).build())
                .flatMap(countryDeleter::delete)
                .thenReturn(ResponseMessageConstant.MSG_DELETED_SUCCESS);
    }

    @Override
    public Mono<TransactionResponse> findByShortCode(TransactionRequest request) {
        return Mono.just(request)
                .filter(this::userIsRequired)
                .map(rq -> Country.builder().shortCode(rq.getParams().get("shortCode")).build())
                .flatMap(countryFinder::findByShortCode)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(BUSINESS_RECORD_NOT_FOUND))))
                .flatMap(c -> this.buildResponse(List.of(c))
                ).switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(BUSINESS_USERNAME_REQUIRED))));
    }

    @Override
    public Mono<String> update(TransactionRequest request) {
        return Mono.just(request)
                .filter(this::userIsRequired)
                .map(TransactionRequest::getItem)
                .flatMap(this::executeUpdate)
                .thenReturn(ResponseMessageConstant.MSG_UPDATED_SUCCESS);
    }

    @Override
    public Mono<Integer> countByStatus(TransactionRequest request) {
        return Mono.just(request)
            .map(TransactionRequest::getItem)
            .flatMap(this::buildCountry)
            .flatMap(c -> countryCounter.countByStatus(c.isStatus()))
            .flatMap(count ->
                redisPort.save(CacheKey.KEY_COUNT_BY_STATUS.getKey(), String.valueOf(count))
                    .thenReturn(count));
    }

    private Boolean userIsRequired(TransactionRequest request){
        return Optional.ofNullable(request)
            .map(TransactionRequest::getContext)
            .map(Context::getCustomer).map(Context.Customer::getUsername)
            .filter(username -> !username.isEmpty())
            .isPresent();
    }

    private Mono<Country> buildCountry(Object object){
        if (object instanceof Country country) {
            return Mono.just(Country.builder().name(country.getName())
                .shortCode(country.getShortCode()).status(country.isStatus())
                .dateCreation(LocalDateTime.now()).description(country.getDescription())
                .build());
        } else {
            return Mono.error(new TechnicalException(TECHNICAL_REQUEST_ERROR));
        }
    }

    private Mono<Country> executeUpdate(Object object){
        if (object instanceof Country country) {
            return countryFinder.findByShortCode(Country.builder().shortCode(country.getShortCode()).build())
                    .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(BUSINESS_RECORD_NOT_FOUND))))
                    .map(ca -> Country.builder().id(ca.getId()).name(country.getName())
                            .shortCode(country.getShortCode()).status(country.isStatus())
                            .dateCreation(country.getDateCreation()).description(country.getDescription())
                            .build())
                    .flatMap(countryUpdater::update);
        } else {
            return Mono.error(new TechnicalException(TECHNICAL_REQUEST_ERROR));
        }
    }

    private Mono<TransactionResponse> buildResponse(List<Country> countries){
        TransactionResponse response = TransactionResponse.builder()
            .message(ResponseMessageConstant.MSG_LIST_SUCCESS)
            .size(countries.size())
            .response(Collections.singletonList(countries))
            .build();

        return Mono.just(response);
    }


}