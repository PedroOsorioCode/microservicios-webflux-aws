package co.com.microservicio.aws.api.greet;

import co.com.microservicio.aws.log.LoggerBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class GreetHandler {
    private static final String NAME_CLASS = GreetHandler.class.getName();
    private static final String MESSAGE_SERVICE = "Service Api Rest greet";

    private final LoggerBuilder logger;

    public Mono<ServerResponse> greet(ServerRequest serverRequest) {
        var headers = serverRequest.headers().asHttpHeaders().toSingleValueMap();
        logger.info(MESSAGE_SERVICE, headers.get("message-id"), "Api Rest", NAME_CLASS);
        return ServerResponse.ok().bodyValue("¡Hi functional, " + headers.get("user-name") + "!");
    }
}
