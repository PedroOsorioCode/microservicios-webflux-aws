package co.com.microservicio.aws.api.worldregion;

import co.com.microservicio.aws.api.greet.doc.GreetOpenAPI;
import co.com.microservicio.aws.api.worldregion.config.ApiWorldRegionProperties;
import lombok.RequiredArgsConstructor;
import org.springdoc.webflux.core.fn.SpringdocRouteBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class WorldRegionRouterRest {
    private final ApiWorldRegionProperties properties;

    @Bean
    public RouterFunction<ServerResponse> routerWorldRegionFunction(WorldRegionHandler worldRegionHandler) {
        return SpringdocRouteBuilder.route()
                .GET(createRoute(properties.getListByRegion()), worldRegionHandler::listByRegion,
                        GreetOpenAPI.greetRoute())
                .GET(createRoute(properties.getFindOne()), worldRegionHandler::findOne,
                        GreetOpenAPI.greetRoute())
                .build();
    }

    private String createRoute(String route){
        return properties.getPathBase().concat(route);
    }
}
