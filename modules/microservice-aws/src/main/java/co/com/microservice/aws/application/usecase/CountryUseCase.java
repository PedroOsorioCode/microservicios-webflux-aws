package co.com.microservice.aws.application.usecase;

import co.com.microservice.aws.domain.model.Country;
import co.com.microservice.aws.domain.model.commons.exception.TechnicalException;
import co.com.microservice.aws.domain.model.commons.util.ResponseMessageConstant;
import co.com.microservice.aws.domain.model.rq.Context;
import co.com.microservice.aws.domain.model.rq.TransactionRequest;
import co.com.microservice.aws.domain.model.rs.TransactionResponse;
import co.com.microservice.aws.domain.usecase.in.ListAllUseCase;
import co.com.microservice.aws.domain.usecase.in.SaveUseCase;
import co.com.microservice.aws.domain.usecase.out.ListAllPort;
import co.com.microservice.aws.domain.usecase.out.SavePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static co.com.microservice.aws.domain.model.commons.enums.TechnicalExceptionMessage.TECHNICAL_REQUEST_ERROR;

@RequiredArgsConstructor
public class CountryUseCase implements SaveUseCase, ListAllUseCase {
    private static final String KEY_USER_NAME = "user-name";
    private static final String ATTRIBUTE_IS_REQUIRED = "The attribute '%s' is required";

    private final SavePort<Country> countrySaver;
    private final ListAllPort<Country> countryLister;

    @Override
    public Mono<TransactionResponse> listAll(TransactionRequest request) {
        return Mono.just(request)
            .filter(this::userIsRequired)
            .flatMap(req -> countryLister.listAll(req.getContext()).collectList().flatMap(this::buildResponse)
            ).switchIfEmpty(Mono.error(
                new IllegalStateException(String.format(ATTRIBUTE_IS_REQUIRED, KEY_USER_NAME))));
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

    private Boolean userIsRequired(TransactionRequest request){
        return Optional.ofNullable(request)
            .map(TransactionRequest::getContext)
            .map(Context::getCustomer).map(Context.Customer::getUsername)
            .filter(username -> !username.isEmpty())
            .isPresent();
    }

    private Mono<Country> buildCountry(Object object){
        if (object instanceof Country country) {
            return Mono.just(Country.builder().id(country.getId()).name(country.getName())
                .shortCode(country.getShortCode()).status(country.isStatus())
                .dateCreation(country.getDateCreation())
                .build());
        } else {
            return Mono.error(new TechnicalException(TECHNICAL_REQUEST_ERROR));
        }
    }

    private Mono<TransactionResponse> buildResponse(List<Country> countries){
        var simplifiedList = countries.stream()
            .map(country -> Country.builder().id(country.getId()).name(country.getName())
                .shortCode(country.getShortCode()).status(country.isStatus())
                .dateCreation(country.getDateCreation())
                .build())
            .toList();

        TransactionResponse response = TransactionResponse.builder()
            .message(ResponseMessageConstant.MSG_LIST_SUCCESS)
            .size(countries.size())
            .response(Collections.singletonList(simplifiedList))
            .build();

        return Mono.just(response);
    }
}