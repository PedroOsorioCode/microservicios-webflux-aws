package co.com.microservice.aws.infrastructure.output.mysql;

import co.com.microservice.aws.application.helpers.logs.LoggerBuilder;
import co.com.microservice.aws.domain.model.Parameter;
import co.com.microservice.aws.domain.usecase.out.FindByNamePort;
import co.com.microservice.aws.infrastructure.output.mysql.mapper.ParameterEntityMapper;
import co.com.microservice.aws.infrastructure.output.mysql.repository.ParameterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ParameterAdapter implements FindByNamePort<Parameter> {
    private final ParameterEntityMapper mapper;
    private final ParameterRepository parameterRepository;
    private final LoggerBuilder logger;

    @Override
    public Mono<Parameter> findByName(Parameter parameter) {
        return Mono.just(parameter)
                .map(Parameter::getName)
                .flatMap(parameterRepository::findByName)
                .map(mapper::toModelFromEntity);
    }
}