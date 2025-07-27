package co.com.microservice.jwt.infrastructure.input.rest.api.router;

import co.com.microservice.jwt.infrastructure.input.rest.api.config.RouterProperties;
import co.com.microservice.jwt.infrastructure.input.rest.api.handler.UserHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class UserRouterRest {
    private final RouterProperties properties;

    @Bean
    public RouterFunction<ServerResponse> routerUserFunction(UserHandler userHandler) {
        return RouterFunctions.route()
            .GET(createRoutePublic(properties.getPathInfo()), userHandler::info)
            .GET(createRoutePrivate(
                properties.getPathUser().concat(properties.getValidateSecurity())), userHandler::validate)
            .build();
    }

    private String createRoutePublic(String route){
        return properties.getPathBase()
                .concat(properties.getPathPublic())
                .concat(route);
    }

    private String createRoutePrivate(String route){
        return properties.getPathBase()
                .concat(properties.getPathPrivate())
                .concat(route);
    }
}