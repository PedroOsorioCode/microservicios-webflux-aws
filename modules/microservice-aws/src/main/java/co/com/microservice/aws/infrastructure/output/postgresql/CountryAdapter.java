package co.com.microservice.aws.infrastructure.output.postgresql;

import co.com.microservice.aws.domain.model.Country;
import co.com.microservice.aws.domain.model.gateway.Save;
import co.com.microservice.aws.domain.model.gateway.Update;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CountryAdapter implements Save<Country>, Update<Country> {
    @Override
    public Mono<Country> save(Country country) {
        return null;
    }

    @Override
    public Mono<Country> update(Country country) {
        return null;
    }
}
