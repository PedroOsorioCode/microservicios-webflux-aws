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
    private static final String MESSAGE_ID = "message-id";

    private final LoggerBuilder logger;

    public Mono<ServerResponse> greet(ServerRequest serverRequest) {
        var headers = serverRequest.headers().asHttpHeaders().toSingleValueMap();
        printOnProcess(headers.get(MESSAGE_ID), "Api Rest simple");
        return ServerResponse.ok().bodyValue("¡Hi functional, " + headers.get("user-name") + "!");
    }

    public Mono<ServerResponse> greetQueryParam(ServerRequest serverRequest) {
        var place = serverRequest.queryParam("place").orElse("");
        var headers = serverRequest.headers().asHttpHeaders().toSingleValueMap();
        printOnProcess(headers.get(MESSAGE_ID), "Api Rest query param");
        return ServerResponse.ok().bodyValue("¡Hi functional query param, " + headers.get("user-name") + "! in " + place);
    }

    public Mono<ServerResponse> greetPathVariable(ServerRequest serverRequest) {
        var place = serverRequest.pathVariable("place");
        var headers = serverRequest.headers().asHttpHeaders().toSingleValueMap();
        printOnProcess(headers.get(MESSAGE_ID), "Api Rest path variable");
        return ServerResponse.ok().bodyValue("¡Hi functional path variable, " + headers.get("user-name") + "! in " + place);
    }

    private void printOnProcess(String messageId, String messageInfo){
        logger.info(MESSAGE_SERVICE, messageId, messageInfo, NAME_CLASS);
    }
}
