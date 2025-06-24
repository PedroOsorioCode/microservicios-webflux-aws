package co.com.microservicio.aws.dynamodb.mapper;

import co.com.microservicio.aws.dynamodb.model.ModelEntityWorldRegion;
import co.com.microservicio.aws.model.worldregion.WorldRegion;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WorldRegionDataMapper {
    ModelEntityWorldRegion toEntity(WorldRegion worldRegion);
    WorldRegion toData(ModelEntityWorldRegion modelEntityWorldRegion);
}
