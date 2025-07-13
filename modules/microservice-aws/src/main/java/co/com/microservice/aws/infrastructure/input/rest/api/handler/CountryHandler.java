package co.com.microservice.aws.infrastructure.input.rest.api.handler;

import co.com.microservice.aws.application.helpers.commons.ContextUtil;
import co.com.microservice.aws.application.helpers.logs.LoggerBuilder;
import co.com.microservice.aws.application.helpers.logs.TransactionLog;
import co.com.microservice.aws.domain.model.Country;
import co.com.microservice.aws.domain.model.rq.Context;
import co.com.microservice.aws.domain.model.rq.TransactionRequest;
import co.com.microservice.aws.domain.usecase.in.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

import static co.com.microservice.aws.domain.model.commons.util.LogMessage.*;

@Component
@RequiredArgsConstructor
public class CountryHandler {
    private static final String NAME_CLASS = CountryHandler.class.getName();
    private static final String EMPTY_VALUE = "";

    private final LoggerBuilder logger;
    private final ListAllUseCase useCaseLister;
    private final SaveUseCase useCaseSaver;
    private final UpdateUseCase useCaseUpdater;
    private final DeleteUseCase useCaseDeleter;
    private final FindByShortCodeUseCase useCaseFinder;

    public Mono<ServerResponse> listAll(ServerRequest serverRequest) {
        var request = this.buildRequestWithParams(serverRequest, METHOD_LISTCOUNTRIES, Map.of("none", "none"));
        return useCaseLister.listAll(request)
                .doOnError(this::printFailed)
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    public Mono<ServerResponse> save(ServerRequest serverRequest) {
        var headers = serverRequest.headers().asHttpHeaders().toSingleValueMap();
        var context = ContextUtil.buildContext(headers);
        printOnProcess(context, METHOD_SAVE);

        return this.getRequest(serverRequest)
                .flatMap(useCaseSaver::save)
                .flatMap(msg -> ServerResponse.ok().bodyValue(msg));
    }

    public Mono<ServerResponse> findOne(ServerRequest serverRequest) {
        var request = this.buildRequestWithParamsFind(serverRequest, METHOD_FINDONE);
        return useCaseFinder.findByShortCode(request)
                .doOnError(this::printFailed)
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        var headers = serverRequest.headers().asHttpHeaders().toSingleValueMap();
        var context = ContextUtil.buildContext(headers);
        printOnProcess(context, METHOD_UPDATE);

        return this.getRequest(serverRequest)
                .flatMap(useCaseUpdater::update)
                .flatMap(msg -> ServerResponse.ok().bodyValue(msg));
    }

    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        var request = this.buildRequestWithParamsDelete(serverRequest, METHOD_DELETE);
        return useCaseDeleter.delete(request)
                .doOnError(this::printFailed)
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    private Mono<TransactionRequest> getRequest(ServerRequest serverRequest) {
        var headers = serverRequest.headers().asHttpHeaders().toSingleValueMap();
        var context = ContextUtil.buildContext(headers);
        return serverRequest.bodyToMono(Country.class)
                .flatMap(country -> Mono.just(TransactionRequest.builder()
                        .context(context).item(country).build()));
    }

    private TransactionRequest buildRequestWithParamsFind(ServerRequest serverRequest, String method){
        var shortCode = serverRequest.pathVariable("shortCode");
        return this.buildRequestWithParams(serverRequest, method, Map.of("shortCode", shortCode));
    }

    private TransactionRequest buildRequestWithParamsDelete(ServerRequest serverRequest, String method){
        var shortCode = serverRequest.pathVariable("id");
        return this.buildRequestWithParams(serverRequest, method, Map.of("id", shortCode));
    }

    private TransactionRequest buildRequestWithParams(ServerRequest serverRequest,
                                                      String method, Map<String, String> param){
        var headers = serverRequest.headers().asHttpHeaders().toSingleValueMap();
        var context = ContextUtil.buildContext(headers);
        printOnProcess(context, method);

        return TransactionRequest.builder()
                .context(context)
                .params(param)
                .build();
    }

    private void printFailed(Throwable throwable) {
        logger.error(throwable);
    }

    private void printOnProcess(Context context, String messageInfo){
        logger.info(TransactionLog.Request.builder().body(context).build(),
                messageInfo, context.getId(), MESSAGE_SERVICE, NAME_CLASS);
    }
}