package co.com.microservice.jwt.infrastructure.input.rest.api.handler;

import co.com.microservice.jwt.application.helpers.logs.LoggerBuilder;
import co.com.microservice.jwt.application.helpers.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SecurityHandler {
    private static final String NAME_CLASS = SecurityHandler.class.getName();
    private static final String MESSAGE_ID = "message-id";
    private static final String USER_NAME = "user-name";
    private final LoggerBuilder logger;
    private final JwtUtil jwtUtil;

    public Mono<ServerResponse> generateToken(ServerRequest serverRequest) {
        var headers = serverRequest.headers().asHttpHeaders().toSingleValueMap();
        printOnProcess(headers.get(MESSAGE_ID), "get token");

        return ServerResponse.ok().bodyValue(
            Map.of("token", jwtUtil.generateToken(headers.get(USER_NAME))));
    }

    private void printOnProcess(String messageId, String messageService){
        logger.info("Security token", messageId, messageService, NAME_CLASS);
    }
}