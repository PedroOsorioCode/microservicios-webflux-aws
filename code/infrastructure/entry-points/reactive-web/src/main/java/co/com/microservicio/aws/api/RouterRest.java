package co.com.microservicio.aws.api;

import co.com.microservicio.aws.api.config.ApiProperties;
import co.com.microservicio.aws.api.doc.OpenApiDoc;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springdoc.webflux.core.fn.SpringdocRouteBuilder;

@Configuration
@RequiredArgsConstructor
public class RouterRest {
    private final ApiProperties properties;

    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return SpringdocRouteBuilder.route()
                .GET(properties.getPathBase().concat(properties.getGetAllRows()),
                        handler::getAllRows, OpenApiDoc.executeListDataExampleOpenApi())
                .build();
    }
}
