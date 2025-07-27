package co.com.microservice.jwt.infrastructure.input.rest.api.handler;

import co.com.microservice.jwt.application.helpers.logs.LoggerBuilder;
import co.com.microservice.jwt.application.helpers.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserHandler {
    private static final String NAME_CLASS = UserHandler.class.getName();
    private static final String MESSAGE_ID = "message-id";
    private final LoggerBuilder logger;
    private final JwtUtil jwtUtil;

    public Mono<ServerResponse> validate(ServerRequest serverRequest) {
        var headers = serverRequest.headers().asHttpHeaders().toSingleValueMap();

        var authHeader = headers.get("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("Token invalid");
        }

        var tokenSinBearer = authHeader.substring(7);
        var user = jwtUtil.getUsername(tokenSinBearer);
        printOnProcess(headers.get(MESSAGE_ID), "validate token user " + user);
        return ServerResponse.ok().bodyValue("Hi validate token, " + user + "!");
    }

    public Mono<ServerResponse> info(ServerRequest serverRequest) {
        var headers = serverRequest.headers().asHttpHeaders().toSingleValueMap();
        printOnProcess(headers.get(MESSAGE_ID), "info user");
        return ServerResponse.ok().bodyValue("Hi info user");
    }

    private void printOnProcess(String messageId, String messageService){
        logger.info("Api Rest simple", messageId, messageService, NAME_CLASS);
    }
}