package co.com.microservice.aws.infrastructure.input.rest.api.exception;

import co.com.microservice.aws.domain.model.commons.error.ErrorFactory;
import co.com.microservice.aws.domain.model.commons.exception.BusinessException;
import co.com.microservice.aws.domain.model.commons.exception.TechnicalException;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Order(-2)
@Component
public class ExceptionHandler extends AbstractErrorWebExceptionHandler {
    public static final String FORMAT_ERROR = "%s:%s";

    public ExceptionHandler(ErrorAttributes errorAttributes, ApplicationContext applicationContext,
                            ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, new WebProperties.Resources(), applicationContext);
        this.setMessageWriters(serverCodecConfigurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::buildErrorResponse);
    }

    public Mono<ServerResponse> buildErrorResponse(final ServerRequest request) {
        return Mono.just(request).map(this::getError).flatMap(Mono::error)
                .onErrorResume(TechnicalException.class, responseTechnicalError(request))
                .onErrorResume(BusinessException.class, responseBusinessError(request))
                .onErrorResume(responseDefaultError(request)).cast(ServerResponse.class);
    }

    private Function<BusinessException, Mono<ServerResponse>> responseBusinessError(ServerRequest request) {
        return e -> Mono
                .just(ErrorFactory.fromBusiness(e,
                        String.format(FORMAT_ERROR, request.method().name(), request.path())))
                .flatMap(this::responseFailBusiness);
    }

    private Function<TechnicalException, Mono<ServerResponse>> responseTechnicalError(ServerRequest request) {
        return e -> Mono
                .just(ErrorFactory.fromTechnical(e,
                        String.format(FORMAT_ERROR, request.method().name(), request.path())))
                .flatMap(this::responseFailBusiness);
    }

    private Function<Throwable, Mono<ServerResponse>> responseDefaultError(ServerRequest request) {
        return exception -> Mono
                .just(ErrorFactory.fromDefaultTechnical(exception.getMessage(),
                        String.format(FORMAT_ERROR, request.method().name(), request.path())))
                .flatMap(this::responseFail);
    }

    public <T> Mono<ServerResponse> buildResponse(T error, HttpStatus httpStatus) {
        return ServerResponse.status(httpStatus).contentType(MediaType.APPLICATION_JSON).bodyValue(error);
    }

    public <T> Mono<ServerResponse> responseFail(T body) {
        return buildResponse(body, INTERNAL_SERVER_ERROR);
    }

    public <T> Mono<ServerResponse> responseFailBusiness(T body) {
        return buildResponse(body, BAD_REQUEST);
    }
}