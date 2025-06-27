package co.com.microservicio.aws.api.worldregion;

import co.com.microservicio.aws.commons.ContextUtil;
import co.com.microservicio.aws.log.LoggerBuilder;
import co.com.microservicio.aws.log.TransactionLog;
import co.com.microservicio.aws.model.worldregion.WorldRegion;
import co.com.microservicio.aws.model.worldregion.rq.Context;
import co.com.microservicio.aws.model.worldregion.rq.TransactionRequest;
import co.com.microservicio.aws.model.worldregion.rs.TransactionResponse;
import co.com.microservicio.aws.usecase.worldregion.WorldRegionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static co.com.microservicio.aws.model.worldregion.util.LogMessage.*;
import static co.com.microservicio.aws.model.worldregion.util.WorldRegionConstant.*;

@Component
@RequiredArgsConstructor
public class WorldRegionHandler {
    private static final String NAME_CLASS = WorldRegionHandler.class.getName();
    private static final String EMPTY_VALUE = "";

    private final LoggerBuilder logger;
    private final WorldRegionUseCase worldRegionUseCase;

    public Mono<ServerResponse> listByRegion(ServerRequest serverRequest) {
        var request = this.buildRequestWithParams(serverRequest, METHOD_LISTCOUNTRIES);
        return ServerResponse.ok().body(worldRegionUseCase.listByRegion(request)
                .onErrorResume(e -> this.printFailed(e, request.getContext().getId())), TransactionResponse.class
        );
    }

    public Mono<ServerResponse> findOne(ServerRequest serverRequest) {
        var request = this.buildRequestWithParams(serverRequest, METHOD_FINDONE);
        return ServerResponse.ok().body(worldRegionUseCase.findOne(request)
            .onErrorResume(e -> this.printFailed(e, request.getContext().getId())), TransactionResponse.class
        );
    }

    public Mono<ServerResponse> save(ServerRequest serverRequest) {
        var headers = serverRequest.headers().asHttpHeaders().toSingleValueMap();
        var context = ContextUtil.buildContext(headers);
        printOnProcess(context, METHOD_SAVE);

        return this.getWorldRegionRequest(serverRequest)
                .flatMap(worldRegionUseCase::save)
                .flatMap(msg -> ServerResponse.ok().bodyValue(msg));
    }

    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        var headers = serverRequest.headers().asHttpHeaders().toSingleValueMap();
        var context = ContextUtil.buildContext(headers);
        printOnProcess(context, METHOD_UPDATE);

        return this.getWorldRegionRequest(serverRequest)
                .flatMap(worldRegionUseCase::update)
                .flatMap(msg -> ServerResponse.ok().bodyValue(msg));
    }

    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        var request = this.buildRequestWithParams(serverRequest, METHOD_DELETE);
        return ServerResponse.ok().body(worldRegionUseCase.delete(request), String.class);
    }

    private Mono<TransactionRequest> getWorldRegionRequest(ServerRequest serverRequest) {
        var headers = serverRequest.headers().asHttpHeaders().toSingleValueMap();
        var context = ContextUtil.buildContext(headers);
        return serverRequest.bodyToMono(WorldRegion.class)
                .flatMap(wr -> Mono.just(TransactionRequest.builder()
                        .context(context).item(wr).build()));
    }

    private TransactionRequest buildRequestWithParams(ServerRequest serverRequest, String method){
        var placeType = serverRequest.queryParam(PARAM_PLACE_TYPE).orElse(EMPTY_VALUE);
        var place = serverRequest.queryParam(PARAM_PLACE).orElse(EMPTY_VALUE);
        var code = serverRequest.queryParam(PARAM_CODE).orElse(EMPTY_VALUE);
        var headers = serverRequest.headers().asHttpHeaders().toSingleValueMap();
        var context = ContextUtil.buildContext(headers);
        printOnProcess(context, method);

        return TransactionRequest.builder()
                .context(context)
                .param(TransactionRequest.Param.builder().placeType(placeType).place(place).code(code).build())
                .build();
    }

    private Mono<TransactionResponse> printFailed(Throwable throwable, String messageId) {
        logger.error(throwable.getMessage(), messageId, MESSAGE_SERVICE, NAME_CLASS);
        return Mono.empty();
    }

    private void printOnProcess(Context context, String messageInfo){
        logger.info(TransactionLog.Request.builder().body(context).build(),
                TransactionLog.Response.builder().build(),
                messageInfo, context.getId(), MESSAGE_SERVICE, NAME_CLASS);
    }
}
