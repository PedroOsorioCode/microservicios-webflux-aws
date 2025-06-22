package co.com.microservicio.aws.api;

import co.com.microservicio.aws.log.LoggerBuilder;
import co.com.microservicio.aws.log.TransactionLog;
import co.com.microservicio.aws.model.flight.FlightTicket;
import co.com.microservicio.aws.usecase.flight.FlightTicketUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class Handler {
    private static final String NAME_CLASS = Handler.class.getName();
    private static final String MESSAGE_SERVICE = "Service Api Rest get alls rows by size";
    private final LoggerBuilder logger;
    private final FlightTicketUseCase flightTicketUseCase;

    public Mono<ServerResponse> getAllRows(ServerRequest serverRequest) {
        var headers = serverRequest.headers().asHttpHeaders().toSingleValueMap();
        var messageId = headers.get("message-id");
        logger.info(TransactionLog.Request.builder().body(headers).build(), null,
            "My first api rest", messageId, MESSAGE_SERVICE, NAME_CLASS);

        return ServerResponse.ok().body(flightTicketUseCase.getAllRows(headers)
            .onErrorResume(e -> this.printFailed(e, messageId)), FlightTicket.class
        );
    }

    private Mono<FlightTicket> printFailed(Throwable throwable, String messageId) {
        logger.error(throwable.getMessage(), messageId, MESSAGE_SERVICE, NAME_CLASS);
        return Mono.empty();
    }
}
