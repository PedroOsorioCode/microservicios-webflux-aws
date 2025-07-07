package co.com.microservicio.aws.usecase.worldregion;

import co.com.microservicio.aws.cache.gateway.CacheGateway;
import co.com.microservicio.aws.commons.exceptions.BusinessException;
import co.com.microservicio.aws.model.worldregion.WorldRegion;
import co.com.microservicio.aws.model.worldregion.gateway.WorldRegionRepository;
import co.com.microservicio.aws.model.worldregion.rq.Context;
import co.com.microservicio.aws.model.worldregion.rq.TransactionRequest;
import co.com.microservicio.aws.model.worldregion.rs.TransactionResponse;
import co.com.microservicio.aws.model.worldregion.rs.WorldRegionResponse;
import co.com.microservicio.aws.model.worldregion.util.WorldRegionConstant;
import co.com.microservicio.aws.restconsumer.Parameter;
import co.com.microservicio.aws.usecase.restconsumer.RestParameterUseCase;
import co.com.microservicio.aws.usecase.sentevent.SentEventUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static co.com.microservicio.aws.commons.enums.BusinessExceptionMessage.BUSINESS_USERNAME_REQUIRED;

@RequiredArgsConstructor
public class WorldRegionUseCase {
    private static final String KEY_USER_NAME = "user-name";
    private static final String ATTRIBUTE_IS_REQUIRED = "The attribute '%s' is required";

    private final WorldRegionRepository regionRepository;
    private final SentEventUseCase sentEventUseCase;
    private final RestParameterUseCase restParameterUseCase;
    private final CacheGateway<Parameter> cacheGateway;

    public Mono<TransactionResponse> listByRegion(TransactionRequest request){
        return Mono.just(request)
            .filter(this::userIsRequired)
            .flatMap(req -> regionRepository.findByRegion(buildKeyRegion(req))
                .collectList().flatMap(this::buildResponse))
            .doOnNext(res -> sentEventUseCase.sendAuditList(request))
            .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(BUSINESS_USERNAME_REQUIRED))));
    }

    public Mono<TransactionResponse> findOne(TransactionRequest request){
        return Mono.just(request)
            .filter(this::userIsRequired)
            .flatMap(req -> regionRepository.findOne(buildKeyRegion(req), request.getParam().getCode()))
            .flatMap(wr -> this.buildResponse(List.of(wr)))
            .switchIfEmpty(Mono.error(new IllegalStateException(String.format(ATTRIBUTE_IS_REQUIRED, KEY_USER_NAME))));
    }

    public Mono<String> save(TransactionRequest request){
        return Mono.just(request)
                .filter(this::userIsRequired)
                .map(TransactionRequest::getItem)
                .map(wr -> WorldRegion
                        .builder()
                        .region(wr.getRegion().toUpperCase())
                        .name(wr.getName())
                        .code(UUID.randomUUID().toString())
                        .codeRegion(wr.getCodeRegion().toUpperCase())
                        .creationDate(new Date().toString()).build())
                .flatMap(regionRepository::save)
                .flatMap(res -> restParameterUseCase.getParameterAuditOnSave(request.getContext()))
                .doOnNext(res -> sentEventUseCase.sendAuditSave(request, res))
                .thenReturn(WorldRegionConstant.MSG_SAVED_SUCCESS);
    }

    public Mono<String> update(TransactionRequest request){
        return Mono.just(request)
                .filter(this::userIsRequired)
                .map(TransactionRequest::getItem)
                .flatMap(regionRepository::update)

                .thenReturn(WorldRegionConstant.MSG_UPDATED_SUCCESS);
    }

    public Mono<String> delete(TransactionRequest request){
        return Mono.just(request)
                .filter(this::userIsRequired)
                .flatMap(req -> regionRepository.delete(buildKeyRegion(req), request.getParam().getCode()))
                .thenReturn(WorldRegionConstant.MSG_DELETED_SUCCESS);
    }

    private Boolean userIsRequired(TransactionRequest request){
        return Optional.ofNullable(request)
                .map(TransactionRequest::getContext)
                .map(Context::getCustomer).map(Context.Customer::getUsername)
                .filter(username -> !username.isEmpty())
                .isPresent();
    }

    private String buildKeyRegion(TransactionRequest request){
        return request.getParam().getPlaceType().toUpperCase()
                .concat(WorldRegionConstant.SEPARATOR_CODE).concat(request.getParam().getPlace().toUpperCase());
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
                .message(WorldRegionConstant.MSG_LIST_SUCCESS)
                .size(worldRegions.size())
                .response(simplifiedList)
                .build();

        return Mono.just(response);
    }

}
