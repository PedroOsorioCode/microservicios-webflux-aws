package co.com.microservice.aws.infrastructure.input.rest.api.handler;

import co.com.microservice.aws.application.helpers.commons.ContextUtil;
import co.com.microservice.aws.application.helpers.logs.LoggerBuilder;
import co.com.microservice.aws.application.helpers.logs.TransactionLog;
import co.com.microservice.aws.domain.model.Country;
import co.com.microservice.aws.domain.model.rq.Context;
import co.com.microservice.aws.domain.model.rq.TransactionRequest;
import co.com.microservice.aws.domain.model.rs.TransactionResponse;
import co.com.microservice.aws.domain.usecase.in.ListAllUseCase;
import co.com.microservice.aws.domain.usecase.in.SaveUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static co.com.microservice.aws.domain.model.commons.util.LogMessage.*;

@Component
@RequiredArgsConstructor
public class CountryHandler {
    private static final String NAME_CLASS = CountryHandler.class.getName();
    private static final String EMPTY_VALUE = "";

    private final LoggerBuilder logger;
    private final ListAllUseCase useCaseLister;
    private final SaveUseCase useCaseSaver;

    public Mono<ServerResponse> listAll(ServerRequest serverRequest) {
        var request = this.buildRequestWithParams(serverRequest, METHOD_LISTCOUNTRIES);
        return ServerResponse.ok().body(useCaseLister.listAll(request)
                .onErrorResume(this::printFailed), TransactionResponse.class
        );
    }

    public Mono<ServerResponse> save(ServerRequest serverRequest) {
        var headers = serverRequest.headers().asHttpHeaders().toSingleValueMap();
        var context = ContextUtil.buildContext(headers);
        printOnProcess(context, METHOD_SAVE);

        return this.getRequest(serverRequest)
                .flatMap(useCaseSaver::save)
                .flatMap(msg -> ServerResponse.ok().bodyValue(msg));
    }

    private Mono<TransactionRequest> getRequest(ServerRequest serverRequest) {
        var headers = serverRequest.headers().asHttpHeaders().toSingleValueMap();
        var context = ContextUtil.buildContext(headers);
        return serverRequest.bodyToMono(Country.class)
                .flatMap(country -> Mono.just(TransactionRequest.builder()
                        .context(context).item(country).build()));
    }

    private TransactionRequest buildRequestWithParams(ServerRequest serverRequest, String method){
        var headers = serverRequest.headers().asHttpHeaders().toSingleValueMap();
        var context = ContextUtil.buildContext(headers);
        printOnProcess(context, method);

        return TransactionRequest.builder()
                .context(context)
                .build();
    }

    private Mono<TransactionResponse> printFailed(Throwable throwable) {
        logger.error(throwable);
        return Mono.empty();
    }

    private void printOnProcess(Context context, String messageInfo){
        logger.info(TransactionLog.Request.builder().body(context).build(),
                messageInfo, context.getId(), MESSAGE_SERVICE, NAME_CLASS);
    }
}