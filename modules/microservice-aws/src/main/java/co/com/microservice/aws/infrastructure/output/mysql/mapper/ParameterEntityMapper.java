package co.com.microservice.aws.infrastructure.output.mysql.mapper;

import co.com.microservice.aws.domain.model.Parameter;
import co.com.microservice.aws.infrastructure.output.mysql.entity.ParameterEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ParameterEntityMapper {
    ParameterEntity toEntityFromModel(Parameter objectModel);
    Parameter toModelFromEntity(ParameterEntity objectEntity);
}