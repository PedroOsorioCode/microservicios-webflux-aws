package co.com.microservicio.aws.api;

import co.com.microservicio.aws.log.LoggerBuilder;
import co.com.microservicio.aws.usecase.flight.FlightTicketUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {
    private static final String MESSAGE_SERVICE = "Service Api Rest get alls rows";
    private final LoggerBuilder logger;
    private final FlightTicketUseCase flightTicketUseCase;

    public Mono<ServerResponse> getAllRows(ServerRequest serverRequest) {
        var headers = serverRequest.headers().asHttpHeaders().toSingleValueMap();
        var messageId = headers.get("message-id");
        logger.info("My first api rest", messageId, MESSAGE_SERVICE, Handler.class.getName());
        return ServerResponse.ok().bodyValue(flightTicketUseCase.getAllRows(messageId));
    }
}
