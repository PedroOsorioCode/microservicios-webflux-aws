package co.com.microservice.aws.infrastructure.output.restconsumer;

import co.com.microservice.aws.application.helpers.logs.LoggerBuilder;
import co.com.microservice.aws.application.helpers.logs.TransactionLog;
import co.com.microservice.aws.domain.model.InfoCountry;
import co.com.microservice.aws.domain.model.commons.exception.TechnicalException;
import co.com.microservice.aws.domain.model.rq.Context;
import co.com.microservice.aws.domain.usecase.out.WorldCountryPort;
import co.com.microservice.aws.infrastructure.output.restconsumer.config.ApiInfoProperties;
import co.com.microservice.aws.infrastructure.output.restconsumer.config.RetryProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Objects;

import static co.com.microservice.aws.domain.model.commons.enums.TechnicalExceptionMessage.TECHNICAL_REST_CLIENT_ERROR;
import static co.com.microservice.aws.domain.model.commons.util.LogMessage.MESSAGE_SERVICE;

@Service
public class WorldCountryAdapter implements WorldCountryPort {
    private static final String NAME_CLASS = WorldCountryAdapter.class.getName();
    private final WebClient webClientConfig;
    private final ApiInfoProperties apiInfoProperties;
    private final RetryProperties retryProperties;
    private final LoggerBuilder logger;

    public WorldCountryAdapter(@Qualifier(value = "webClientConfig") WebClient webClientConfig,
                            ApiInfoProperties apiInfoProperties, LoggerBuilder loggerBuilder,
                            RetryProperties retryProperties){
        this.webClientConfig = webClientConfig.mutate().build();
        this.apiInfoProperties = apiInfoProperties;
        this.logger = loggerBuilder;
        this.retryProperties = retryProperties;
    }

    @Override
    public Mono<Boolean> exist(Context context, String name) {
        logger.info("rest get info country", context.getId(), NAME_CLASS, "exist");
        return this.getCountry(context, apiInfoProperties.getExist(), name);
    }

    private Mono<Boolean> getCountry(Context context, String urlPath, String name) {
        return this.buildGetRequestWithHeaders(context, urlPath.concat(name))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, res -> this.errorStatusFunction(res, context))
                .bodyToMono(InfoCountry.class)
                .retryWhen(Retry
                        .fixedDelay(retryProperties.getRetries(), Duration.ofSeconds(retryProperties.getRetryDelay()))
                        .doBeforeRetry(signal -> this.printErrorRetry(signal, context))
                        .doAfterRetry(retrySignal -> logger.info("Retry: " + (retrySignal.totalRetries() + 1), context.getId(), NAME_CLASS, "isAuditOnList")))
                .doOnNext(res -> this.printOnProcess(context, res))
                .doOnError(logger::error)
                .onErrorMap(original -> new TechnicalException(TECHNICAL_REST_CLIENT_ERROR))
                .flatMap(this::createResponse);
    }

    private Mono<Boolean> createResponse(InfoCountry result) {
        return !Objects.isNull(result) && !result.getCode().isEmpty() ?
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
                TECHNICAL_REST_CLIENT_ERROR.getDescription(),
                (retrySignal.totalRetries() + 1));
        logger.info(messageInfo, context.getId(), NAME_CLASS, "printErrorRetry");
        logger.error(retrySignal.failure());
    }

    private Mono<Throwable> errorStatusFunction(ClientResponse response, Context context) {
        var messageInfo = String.format("rest get info country %s", response.statusCode());
        logger.info(messageInfo, context.getId(), NAME_CLASS, "errorStatusFunction");
        return response.bodyToMono(String.class).switchIfEmpty(Mono.just(response.statusCode().toString()))
                .map(msg -> new TechnicalException(new RuntimeException(msg),
                        TECHNICAL_REST_CLIENT_ERROR));
    }

    private void printOnProcess(Context context, InfoCountry infoCountry){
        logger.info(TransactionLog.Request.builder().body(context).build(),
                context.getId(), context.getId(), MESSAGE_SERVICE, NAME_CLASS);
        logger.info(TransactionLog.Response.builder().body(infoCountry).build(),
                context.getId(), context.getId(), MESSAGE_SERVICE, NAME_CLASS);
    }
}