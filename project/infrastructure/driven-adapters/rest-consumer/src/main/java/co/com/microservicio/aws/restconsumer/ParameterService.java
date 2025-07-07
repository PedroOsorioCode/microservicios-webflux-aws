package co.com.microservicio.aws.restconsumer;

import co.com.microservicio.aws.commons.enums.TechnicalExceptionMessage;
import co.com.microservicio.aws.commons.exceptions.TechnicalException;
import co.com.microservicio.aws.log.LoggerBuilder;
import co.com.microservicio.aws.log.TransactionLog;
import co.com.microservicio.aws.restconsumer.gateway.ParameterGateway;
import co.com.microservicio.aws.restconsumer.properties.ParamProperties;
import co.com.microservicio.aws.restconsumer.properties.RetryProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import reactor.core.publisher.Mono;
import co.com.microservicio.aws.model.worldregion.rq.Context;
import org.springframework.http.HttpStatusCode;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Objects;

import static co.com.microservicio.aws.model.worldregion.util.LogMessage.MESSAGE_SERVICE;

@Service
public class ParameterService implements ParameterGateway {
    private static final String NAME_CLASS = ParameterService.class.getName();
    private static final String AUDIT_ON_LIST_TRUE = "1";
    private final WebClient webClientConfig;
    private final ParamProperties paramProperties;
    private final RetryProperties retryProperties;
    private final LoggerBuilder logger;

    public ParameterService(@Qualifier(value = "webClientConfig") WebClient webClientConfig,
                            ParamProperties paramProperties, LoggerBuilder loggerBuilder,
                            RetryProperties retryProperties){
        this.webClientConfig = webClientConfig.mutate().build();
        this.paramProperties = paramProperties;
        this.logger = loggerBuilder;
        this.retryProperties = retryProperties;
    }

    @Override
    public Mono<Boolean> isAuditOnSave(Context context) {
        logger.info("rest get parameter", context.getId(), NAME_CLASS, "isAuditOnList");
        return this.getParameter(context, paramProperties.getNameAuditOnSave());
    }

    @Override
    public Mono<Boolean> isAuditOnUpdate(Context context) {
        logger.info("rest get parameter", context.getId(), NAME_CLASS, "isAuditOnUpdate");
        return this.getParameter(context, paramProperties.getNameAuditOnUpdate());
    }

    private Mono<Boolean> getParameter(Context context, String urlPath) {
        return this.buildGetRequestWithHeaders(context, urlPath)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatusCode::isError, res -> this.errorStatusFunction(res, context))
            .bodyToMono(Parameter.class)
            .retryWhen(Retry
                .fixedDelay(retryProperties.getRetries(), Duration.ofSeconds(retryProperties.getRetryDelay()))
                .doBeforeRetry(signal -> this.printErrorRetry(signal, context))
                .doAfterRetry(retrySignal -> logger.info("Retry: " + (retrySignal.totalRetries() + 1), context.getId(), NAME_CLASS, "isAuditOnList")))
            .doOnNext(paramres -> this.printOnProcess(context, paramres))
            .doOnError(logger::error)
            .flatMap(this::createResponse);
    }

    private Mono<Boolean> createResponse(Parameter result) {
        return !Objects.isNull(result) && !result.getValue().isEmpty()
                && result.getValue().equalsIgnoreCase(AUDIT_ON_LIST_TRUE) ?
                Mono.just(Boolean.TRUE): Mono.just(Boolean.FALSE);
    }

    private RequestHeadersSpec<?> buildGetRequestWithHeaders(Context context, String urlPath) {
        return webClientConfig.get().uri(urlPath)
                .header("message-id", context.getId())
                .header("ip", context.getCustomer().getIp())
                .header("user-name", context.getCustomer().getUsername());
    }

    private void printErrorRetry(Retry.RetrySignal retrySignal, Context context){
        var messageInfo = String.format("%s, wating retry: '%s'",
                TechnicalExceptionMessage.TECHNICAL_REST_CLIENT_ERROR.getDescription(),
                (retrySignal.totalRetries() + 1));
        logger.error(messageInfo, context.getId(), NAME_CLASS, "printErrorRetry");
        logger.error(retrySignal.failure());
    }

    private Mono<Throwable> errorStatusFunction(ClientResponse response, Context context) {
        var messageInfo = String.format("rest get parameter %s", response.statusCode());
        logger.error(messageInfo, context.getId(), NAME_CLASS, "errorStatusFunction");
        return response.bodyToMono(String.class).switchIfEmpty(Mono.just(response.statusCode().toString()))
                .map(msg -> new TechnicalException(new RuntimeException(msg),
                        TechnicalExceptionMessage.TECHNICAL_REST_CLIENT_ERROR));
    }

    private void printOnProcess(Context context, Parameter parameter){
        logger.info(TransactionLog.Request.builder().body(context).build(),
                TransactionLog.Response.builder().body(parameter).build(),
                context.getId(), context.getId(), MESSAGE_SERVICE, NAME_CLASS);
    }
}
