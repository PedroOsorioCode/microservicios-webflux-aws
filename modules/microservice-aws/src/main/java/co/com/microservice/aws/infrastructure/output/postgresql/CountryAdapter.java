package co.com.microservice.aws.infrastructure.output.postgresql;

import co.com.microservice.aws.domain.model.Country;
import co.com.microservice.aws.domain.model.rq.Context;
import co.com.microservice.aws.domain.usecase.out.ListAllPort;
import co.com.microservice.aws.domain.usecase.out.SavePort;
import co.com.microservice.aws.infrastructure.output.postgresql.mapper.CountryEntityMapper;
import co.com.microservice.aws.infrastructure.output.postgresql.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CountryAdapter implements SavePort<Country>, ListAllPort<Country> {
    private final CountryEntityMapper mapper;
    private final CountryRepository countryRepository;

    @Override
    public Mono<Country> save(Country country, Context context) {
        return Mono.just(country)
                .map(mapper::toEntityFromModel)
                .flatMap(countryRepository::save)
                .map(mapper::toModelFromEntity);
    }

    @Override
    public Flux<Country> listAll(Context context) {
        return countryRepository.findAll()
                .map(mapper::toModelFromEntity);
    }
}