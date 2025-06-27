package co.com.microservicio.aws.api.greet.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "entries.reactive-web")
public class ApiProperties {
    private String pathBase;
    private String greet;
    private String greetReactive;
    private String greetReactiveQueryParam;
    private String greetReactivePathVariable;
}
