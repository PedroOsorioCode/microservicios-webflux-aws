package co.com.microservice.jwt.infrastructure.input.rest.api.router;

import co.com.microservice.jwt.infrastructure.input.rest.api.config.RouterProperties;
import co.com.microservice.jwt.infrastructure.input.rest.api.handler.SecurityHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class SecurityRouterRest {
    private final RouterProperties properties;

    @Bean
    public RouterFunction<ServerResponse> routerSecurityFunction(SecurityHandler securityHandler) {
        return RouterFunctions.route()
                .GET(createRoute(properties.getToken()), securityHandler::generateToken)
                .build();
    }

    private String createRoute(String route){
        return properties.getPathBase()
                .concat(properties.getPathPublic())
                .concat(route);
    }
}