package co.com.microservicio.aws.api.greet;

import co.com.microservicio.aws.api.greet.config.ApiProperties;
import co.com.microservicio.aws.api.greet.doc.GreetOpenAPI;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springdoc.webflux.core.fn.SpringdocRouteBuilder;

@Configuration
@RequiredArgsConstructor
public class GreetRouterRest {
    private final ApiProperties properties;

    @Bean
    public RouterFunction<ServerResponse> routerFunction(GreetHandler greetHandler) {
        return SpringdocRouteBuilder.route()
            .GET(properties.getPathBase().concat(properties.getGreetReactive()),
                greetHandler::greet, GreetOpenAPI.greetRoute())
            .build();
    }
}