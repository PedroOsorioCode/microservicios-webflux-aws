package co.com.microservice.aws.application.config;

import co.com.microservice.aws.application.helpers.logs.LoggerBuilder;
import co.com.microservice.aws.application.helpers.logs.TransactionLog;
import co.com.microservice.aws.domain.model.rq.TransactionRequest;
import co.com.microservice.aws.domain.usecase.in.ParameterUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ParameterLoaderConfig {
    private final ParameterUseCase useCasefinder;
    private final LoggerBuilder logger;

    @Order(1)
    @EventListener(ApplicationReadyEvent.class)
    public void initialParamterStatus() {
        var parameters = Map.of("param1", "Aply_audit", "param2", "Message_in_spanish");
        TransactionRequest req = TransactionRequest.builder().params(parameters).build();

        useCasefinder.findByName(req)
                .doOnNext(param -> logger.info(
                        TransactionLog.Response.builder().body(param).build(),
                        "List parameters", UUID.randomUUID().toString(),
                        "ParameterLoaderConfig", "initialParamterStatus"))
                .doOnError(error -> logger.info("Error al cargar parámetros: " + error.getMessage(), "", "", ""))
                .subscribe();
    }
}