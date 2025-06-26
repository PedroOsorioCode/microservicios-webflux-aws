package co.com.microservicio.aws.usecase.worldregion;

import co.com.microservicio.aws.model.worldregion.WorldRegion;
import co.com.microservicio.aws.model.worldregion.gateway.WorldRegionRepository;
import co.com.microservicio.aws.model.worldregion.rq.TransactionRequest;
import co.com.microservicio.aws.model.worldregion.rq.Context;
import co.com.microservicio.aws.model.worldregion.rq.Customer;
import co.com.microservicio.aws.model.worldregion.rs.TransactionResponse;
import co.com.microservicio.aws.model.worldregion.rs.WorldRegionResponse;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static co.com.microservicio.aws.model.worldregion.util.WorldRegionConstant.MSG_LIST_SUCCESS;
import static co.com.microservicio.aws.model.worldregion.util.WorldRegionConstant.SEPARATOR_CODE;

@RequiredArgsConstructor
public class WorldRegionUseCase {
    private static final String KEY_USER_NAME = "user-name";
    private static final String ATTRIBUTE_IS_REQUIRED = "The attribute '%s' is required";

    private final WorldRegionRepository regionRepository;

    public Mono<TransactionResponse> listByRegion(TransactionRequest request){
        return Mono.just(request)
            .filter(this::userIsRequired)
            .flatMap(req -> regionRepository.findByRegion(buildKeyRegion(req))
                    .collectList().flatMap(this::buildResponse)
            ).switchIfEmpty(Mono.error(new IllegalStateException(
                    String.format(ATTRIBUTE_IS_REQUIRED, KEY_USER_NAME))));
    }

    public Mono<TransactionResponse> findOne(TransactionRequest request){
        return Mono.just(request)
                .filter(this::userIsRequired)
                .flatMap(req -> regionRepository.findOne(buildKeyRegion(req), request.getCode()))
                .flatMap(wr -> this.buildResponse(List.of(wr)))
                .switchIfEmpty(Mono.error(new IllegalStateException(
                        String.format(ATTRIBUTE_IS_REQUIRED, KEY_USER_NAME))));
    }

    private Boolean userIsRequired(TransactionRequest request){
        return Optional.ofNullable(request)
                .map(TransactionRequest::getContext)
                .map(Context::getCustomer)
                .map(Customer::getUsername)
                .filter(username -> !username.isEmpty())
                .isPresent();
    }

    private String buildKeyRegion(TransactionRequest request){
        return request.getPlaceType().toUpperCase()
                .concat(SEPARATOR_CODE).concat(request.getPlace().toUpperCase());
    }

    private Mono<TransactionResponse> buildResponse(List<WorldRegion> worldRegions){
        var simplifiedList = worldRegions.stream()
            .map(wr -> WorldRegionResponse.builder()
                .code(wr.getCode())
                .name(wr.getName())
                .codeRegion(wr.getCodeRegion())
                .creationDate(wr.getCreationDate())
                .build())
            .toList();

        TransactionResponse response = TransactionResponse.builder()
                .message(MSG_LIST_SUCCESS)
                .size(worldRegions.size())
                .response(simplifiedList)
                .build();

        return Mono.just(response);
    }
}
