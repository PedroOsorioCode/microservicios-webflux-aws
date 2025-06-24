package co.com.microservicio.aws.dynamodb;

import co.com.microservicio.aws.dynamodb.config.DynamoDBTablesProperties;
import co.com.microservicio.aws.dynamodb.mapper.WorldRegionDataMapper;
import co.com.microservicio.aws.dynamodb.model.ModelEntityWorldRegion;
import co.com.microservicio.aws.model.worldregion.WorldRegion;
import co.com.microservicio.aws.model.worldregion.gateway.WorldRegionRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

@Component
public class WorldRegionRepositoryAdapter extends
        AdapterOperations<WorldRegion, ModelEntityWorldRegion>
        implements WorldRegionRepository {

    public WorldRegionRepositoryAdapter(DynamoDbEnhancedAsyncClient dbEnhancedAsyncClient,
                                        DynamoDBTablesProperties tablesProperties,
                                        WorldRegionDataMapper mapper) {

        super(dbEnhancedAsyncClient, tablesProperties, mapper::toEntity,
                mapper::toData, ModelEntityWorldRegion.class);

    }

    @Override
    public Flux<WorldRegion> findByEntityType(String entityType) {
        QueryEnhancedRequest request = QueryEnhancedRequest
                .builder()
                .queryConditional(QueryConditional.keyEqualTo(buildKey(entityType)))
                .build();

        return super.findByIndexWithQuery("EntityTypeIndex", request);
    }

    @Override
    public Flux<WorldRegion> findByParentCodeAndEntityType(String parentCode, String entityType) {
        return null;
    }

    private Key buildKey(String sortValue, String partitionValue) {
        return Key.builder().sortValue(sortValue).partitionValue(partitionValue).build();
    }

    private Key buildKey(String partitionValue) {
        return Key.builder().partitionValue(partitionValue).build();
    }
}
