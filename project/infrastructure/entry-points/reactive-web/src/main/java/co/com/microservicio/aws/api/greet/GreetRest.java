package co.com.microservicio.aws.api.greet;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class GreetRest {

    @GetMapping("${entries.reactive-web.path-base}${entries.reactive-web.greet}/{name}")
    public Mono<String> greet(@PathVariable("name") String name) {
        return Mono.just("¡Hi parametrizable, " + name + "!");
    }

    @GetMapping("${entries.reactive-web.path-base}${entries.reactive-web.greet}/header")
    public Mono<String> greetFromHeader(@RequestHeader("user-name") String name) {
        return Mono.just("¡Hi parametrizable, " + name + "!");
    }

    @GetMapping("${entries.reactive-web.path-base}${entries.reactive-web.greet}")
    public Mono<String> genericGreet() {
        return Mono.just("¡Hello parametrizable, world!");
    }
}
