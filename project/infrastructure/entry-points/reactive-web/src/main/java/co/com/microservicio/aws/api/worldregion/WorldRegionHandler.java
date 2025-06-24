package co.com.microservicio.aws.api.worldregion;

import co.com.microservicio.aws.commons.ContextUtil;
import co.com.microservicio.aws.log.LoggerBuilder;
import co.com.microservicio.aws.log.TransactionLog;
import co.com.microservicio.aws.model.worldregion.rq.Context;
import co.com.microservicio.aws.model.worldregion.rq.TransactionRequest;
import co.com.microservicio.aws.model.worldregion.rs.TransactionResponse;
import co.com.microservicio.aws.usecase.worldregion.WorldRegionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class WorldRegionHandler {
    private static final String NAME_CLASS = WorldRegionHandler.class.getName();
    private static final String MESSAGE_SERVICE = "Service Api Rest world regions";

    private final LoggerBuilder logger;
    private final WorldRegionUseCase worldRegionUseCase;

    public Mono<ServerResponse> listAllCountries(ServerRequest serverRequest) {
        var headers = serverRequest.headers().asHttpHeaders().toSingleValueMap();
        var context = ContextUtil.buildContext(headers);
        printOnProcess(context, "List all countries");

        var request = TransactionRequest.builder().context(context).build();
        return ServerResponse.ok().body(worldRegionUseCase.listAllCountries(request)
                .onErrorResume(e -> this.printFailed(e, context.getId())), TransactionResponse.class
        );
    }

    private Mono<TransactionResponse> printFailed(Throwable throwable, String messageId) {
        logger.error(throwable.getMessage(), messageId, MESSAGE_SERVICE, NAME_CLASS);
        return Mono.empty();
    }

    private void printOnProcess(Context context, String messageInfo){
        logger.info(TransactionLog.Request.builder().body(context).build(), null,
                messageInfo, context.getId(), MESSAGE_SERVICE, NAME_CLASS);
    }
}
