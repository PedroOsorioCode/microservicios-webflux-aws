package co.com.microservice.aws.infrastructure.output.postgresql.mapper;

import co.com.microservice.aws.domain.model.Country;
import co.com.microservice.aws.infrastructure.output.postgresql.entity.CountryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CountryEntityMapper {
    CountryEntity toEntityFromModel(Country objectModel);
    Country toModelFromEntity(CountryEntity objectEntity);
}