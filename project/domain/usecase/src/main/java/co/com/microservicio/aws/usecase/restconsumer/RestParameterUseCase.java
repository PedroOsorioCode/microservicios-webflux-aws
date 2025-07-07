package co.com.microservicio.aws.usecase.restconsumer;

import co.com.microservicio.aws.cache.gateway.CacheGateway;
import co.com.microservicio.aws.model.worldregion.rq.Context;
import co.com.microservicio.aws.restconsumer.Parameter;
import co.com.microservicio.aws.restconsumer.gateway.ParameterGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import co.com.microservicio.aws.cache.CacheKey;

import java.util.UUID;

@RequiredArgsConstructor
public class RestParameterUseCase {
    private final CacheGateway<Boolean> cacheApplicationParameter;
    private final ParameterGateway parameterGateway;

    public Mono<Boolean> getParameterAuditOnSave(Context context){
        return parameterGateway.isAuditOnSave(context)
            .onErrorResume(ex -> Mono.just(Boolean.FALSE));
    }

    public Mono<Boolean> saveParameterAuditOnSave(){
        return parameterGateway.isAuditOnSave(buildContext())
                .onErrorResume(ex -> Mono.just(Boolean.FALSE))
                .flatMap(pv ->
                    cacheApplicationParameter
                            .saveDataInCacheFromEvent(CacheKey.AUDIT_ON_SAVE.getKey(), pv)
                            .thenReturn(pv))
                .defaultIfEmpty(Boolean.FALSE)
                .onErrorReturn(Boolean.FALSE);
    }

    public Mono<Boolean> saveParameterAuditOnUpdate(){
        return parameterGateway.isAuditOnUpdate(buildContext())
                .onErrorResume(ex -> Mono.just(Boolean.FALSE))
                .flatMap(pv ->
                        cacheApplicationParameter
                            .saveDataInCacheFromEvent(CacheKey.AUDIT_ON_UPDATE.getKey(), pv)
                            .thenReturn(pv))
                .defaultIfEmpty(Boolean.FALSE)
                .onErrorReturn(Boolean.FALSE);
    }

    private Context buildContext(){
        return Context.builder()
                .id(UUID.randomUUID().toString())
                .customer(Context.Customer.builder().username("Started app").build())
                .build();
    }
}