package co.com.microservice.aws.infrastructure.output.postgresql.mapper;

import co.com.microservice.aws.domain.model.Country;
import co.com.microservice.aws.infrastructure.output.postgresql.entity.CountryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface CountryEntityMapper {
    CountryEntity toEntityFromModel(Country objectModel);
    Country toModelFromEntity(CountryEntity objectEntity);
}