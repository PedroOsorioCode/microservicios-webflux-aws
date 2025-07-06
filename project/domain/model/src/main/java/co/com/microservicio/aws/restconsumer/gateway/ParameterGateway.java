package co.com.microservicio.aws.restconsumer.gateway;

import co.com.microservicio.aws.model.worldregion.rq.Context;
import reactor.core.publisher.Mono;

public interface ParameterGateway {
    Mono<Boolean> isAuditOnSave(Context context);
}
