package co.com.microservice.aws.infrastructure.input.rest.api.router;

import co.com.microservice.aws.infrastructure.input.rest.api.config.RouterProperties;
import co.com.microservice.aws.infrastructure.input.rest.api.handler.CountryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class CountryRouterRest {
    private final RouterProperties properties;

    @Bean
    public RouterFunction<ServerResponse> routerCountryFunction(CountryHandler countryHandler) {
        return RouterFunctions.route()
                .GET(createRoute(properties.getListAll()), countryHandler::listAll)
                .POST(createRoute(properties.getSave()), countryHandler::save)
                .DELETE(createRoute(properties.getDelete()), countryHandler::delete)
                .PUT(createRoute(properties.getUpdate()), countryHandler::update)
                .GET(createRoute(properties.getFindByShortCode()), countryHandler::findOne)
                .build();
    }

    private String createRoute(String route){
        return properties.getPathBase().concat(properties.getPathCountries()).concat(route);
    }
}