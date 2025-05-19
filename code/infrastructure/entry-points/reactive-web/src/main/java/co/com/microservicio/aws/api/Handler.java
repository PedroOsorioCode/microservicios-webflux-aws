package co.com.microservicio.aws.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {
    public Mono<ServerResponse> getAllRows(ServerRequest serverRequest) {
        return ServerResponse.ok().bodyValue("My first apy rest");
    }
}
