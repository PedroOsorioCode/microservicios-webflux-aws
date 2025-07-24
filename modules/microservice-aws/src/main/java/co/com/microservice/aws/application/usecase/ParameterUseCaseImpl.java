package co.com.microservice.aws.application.usecase;

import co.com.microservice.aws.application.helpers.commons.UseCase;
import co.com.microservice.aws.domain.model.Parameter;
import co.com.microservice.aws.domain.model.commons.enums.CacheKey;
import co.com.microservice.aws.domain.model.commons.util.ResponseMessageConstant;
import co.com.microservice.aws.domain.model.rq.TransactionRequest;
import co.com.microservice.aws.domain.model.rs.TransactionResponse;
import co.com.microservice.aws.domain.usecase.in.ParameterUseCase;
import co.com.microservice.aws.domain.usecase.out.FindByNamePort;
import co.com.microservice.aws.domain.usecase.out.RedisPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@UseCase
@RequiredArgsConstructor
public class ParameterUseCaseImpl implements ParameterUseCase {
    private final FindByNamePort<Parameter> parameterFinder;
    private final RedisPort redisPort;

    @Override
    public Mono<TransactionResponse> findByName(TransactionRequest request) {
        return Mono.just(request)
                .map(rq -> Parameter.builder().name(rq.getParams().get("param1")).build())
                .flatMap(parameterFinder::findByName)
                .flatMap(pv -> redisPort.save(CacheKey.APPLY_AUDIT.getKey(), pv.toString()).thenReturn(pv))
                .flatMap(c -> this.buildResponse(List.of(c)));
    }

    private Mono<TransactionResponse> buildResponse(List<Parameter> parameters){
        TransactionResponse response = TransactionResponse.builder()
                .message(ResponseMessageConstant.MSG_LIST_SUCCESS)
                .size(parameters.size())
                .response(new ArrayList<>(parameters))
                .build();

        return Mono.just(response);
    }
}