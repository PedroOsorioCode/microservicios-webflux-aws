package co.com.microservice.aws.application.usecase;

import co.com.microservice.aws.application.common.UseCase;
import co.com.microservice.aws.domain.model.Country;
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
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static co.com.microservice.aws.domain.model.commons.enums.BusinessExceptionMessage.BUSINESS_RECORD_NOT_FOUND;
import static co.com.microservice.aws.domain.model.commons.enums.BusinessExceptionMessage.BUSINESS_USERNAME_REQUIRED;
import static co.com.microservice.aws.domain.model.commons.enums.TechnicalExceptionMessage.TECHNICAL_REQUEST_ERROR;

@UseCase
@RequiredArgsConstructor
public class CountryUseCase implements SaveUseCase, ListAllUseCase, FindByShortCodeUseCase, UpdateUseCase, DeleteUseCase {
    private static final String KEY_USER_NAME = "user-name";
    private static final String ATTRIBUTE_IS_REQUIRED = "The attribute '%s' is required";

    private final SavePort<Country> countrySaver;
    private final ListAllPort<Country> countryLister;
    private final UpdatePort<Country> countryUpdater;
    private final DeletePort<Country> countryDeleter;
    private final FindByShortCodePort<Country> countryFinder;

    @Override
    public Mono<TransactionResponse> listAll(TransactionRequest request) {
        return Mono.just(request)
            .filter(this::userIsRequired)
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
                .dateCreation(country.getDateCreation()).description(country.getDescription())
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