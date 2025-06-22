package co.com.microservicio.aws.api.greet;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/microservice-aws/greethardcoded")
public class GreetHardCodedRest {

    @GetMapping("/{name}")
    public Mono<String> greet(@PathVariable String name) {
        return Mono.just("¡Hi, " + name + "!");
    }

    @GetMapping("/header")
    public Mono<String> greetFromHeader(@RequestHeader("user-name") String name) {
        return Mono.just("¡Hi, " + name + "!");
    }

    @GetMapping
    public Mono<String> genericGreet() {
        return Mono.just("¡Hello, world!");
    }
}
