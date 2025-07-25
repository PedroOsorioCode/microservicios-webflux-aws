package co.com.microservice.aws.infrastructure.output.dynamodb.mapper;

import co.com.microservice.aws.domain.model.WorldRegion;
import co.com.microservice.aws.infrastructure.output.dynamodb.model.ModelEntityWorldRegion;
import org.springframework.stereotype.Component;

@Component
public class WorldRegionDataMapper {

    public ModelEntityWorldRegion toEntity(WorldRegion worldRegion) {
        if (worldRegion == null) return null;

        ModelEntityWorldRegion entity = new ModelEntityWorldRegion();
        entity.setRegion(worldRegion.getRegion());
        entity.setCode(worldRegion.getCode());
        entity.setName(worldRegion.getName());
        entity.setCodeRegion(worldRegion.getCodeRegion());
        entity.setCreationDate(worldRegion.getCreationDate());

        return entity;
    }

    public WorldRegion toData(ModelEntityWorldRegion entity) {
        if (entity == null) return null;

        return WorldRegion.builder()
                .region(entity.getRegion())
                .code(entity.getCode())
                .name(entity.getName())
                .codeRegion(entity.getCodeRegion())
                .creationDate(entity.getCreationDate())
                .build();
    }
}