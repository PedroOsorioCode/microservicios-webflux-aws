package co.com.microservicio.aws.usecase.restconsumer;

import co.com.microservicio.aws.model.worldregion.rq.Context;
import co.com.microservicio.aws.restconsumer.gateway.ParameterGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RestParameterUseCase {
    private final ParameterGateway parameterGateway;

    public Mono<Boolean> getParameterAuditOnSave(Context context){
        return parameterGateway.isAuditOnSave(context)
            .onErrorResume(ex -> Mono.just(Boolean.FALSE));
    }
}