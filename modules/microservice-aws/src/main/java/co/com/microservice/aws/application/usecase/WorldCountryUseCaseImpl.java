package co.com.microservice.aws.application.usecase;

import co.com.microservice.aws.application.helpers.commons.UseCase;
import co.com.microservice.aws.domain.model.Country;
import co.com.microservice.aws.domain.model.commons.exception.TechnicalException;
import co.com.microservice.aws.domain.model.commons.util.ResponseMessageConstant;
import co.com.microservice.aws.domain.model.rq.TransactionRequest;
import co.com.microservice.aws.domain.model.rs.TransactionResponse;
import co.com.microservice.aws.domain.usecase.in.WorldCountryUseCase;
import co.com.microservice.aws.domain.usecase.out.WorldCountryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static co.com.microservice.aws.domain.model.commons.enums.TechnicalExceptionMessage.TECHNICAL_REQUEST_ERROR;

@UseCase
@RequiredArgsConstructor
public class WorldCountryUseCaseImpl implements WorldCountryUseCase {
    private final WorldCountryPort worldCountryPort;

    @Override
    public Mono<TransactionResponse> findByName(TransactionRequest request) {
        return Mono.just(request)
                .map(TransactionRequest::getItem)
                .flatMap(this::buildCountry)
                .flatMap(c -> worldCountryPort.exist(request.getContext(), c.getName()))
                .flatMap(res -> this.buildResponse(List.of(res)));
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

    private Mono<TransactionResponse> buildResponse(List<Boolean> existCountries){
        TransactionResponse response = TransactionResponse.builder()
                .message(ResponseMessageConstant.MSG_LIST_SUCCESS)
                .size(existCountries.size())
                .response(new ArrayList<>(existCountries))
                .build();

        return Mono.just(response);
    }
}