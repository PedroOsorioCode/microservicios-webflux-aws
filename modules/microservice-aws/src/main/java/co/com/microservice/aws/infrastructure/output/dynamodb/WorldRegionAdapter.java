package co.com.microservice.aws.infrastructure.output.dynamodb;

import co.com.microservice.aws.application.helpers.logs.LoggerBuilder;
import co.com.microservice.aws.application.helpers.logs.TransactionLog;
import co.com.microservice.aws.domain.model.WorldRegion;
import co.com.microservice.aws.domain.model.rq.Context;
import co.com.microservice.aws.domain.usecase.out.WorldRegionPort;
import co.com.microservice.aws.infrastructure.output.dynamodb.config.DynamoDBTablesProperties;
import co.com.microservice.aws.infrastructure.output.dynamodb.mapper.WorldRegionDataMapper;
import co.com.microservice.aws.infrastructure.output.dynamodb.model.ModelEntityWorldRegion;
import co.com.microservice.aws.infrastructure.output.dynamodb.operations.AdapterOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

@Component
public class WorldRegionAdapter extends AdapterOperations<WorldRegion, ModelEntityWorldRegion>
            implements WorldRegionPort {
    private LoggerBuilder logger;

    public WorldRegionAdapter(DynamoDbEnhancedAsyncClient dbEnhancedAsyncClient,
            DynamoDBTablesProperties tablesProperties,
            WorldRegionDataMapper mapper, LoggerBuilder loggerBuilder) {

        super(dbEnhancedAsyncClient, tablesProperties, mapper::toEntity,
                mapper::toData, ModelEntityWorldRegion.class);

        this.logger = loggerBuilder;

    }

    @Override
    public Flux<WorldRegion> findByRegion(Context context, String region) {
        QueryEnhancedRequest request = QueryEnhancedRequest
                .builder()
                .queryConditional(QueryConditional.keyEqualTo(buildKey(region)))
                .build();

        return super.findByQuery(request);
    }

    @Override
    public Mono<WorldRegion> findOne(Context context, String region, String code) {
        return super.findOne(buildKey(region, code));
    }

    @Override
    public Mono<WorldRegion> save(Context context, WorldRegion worldRegion) {
        logger.info("save dynamodb", context.getId(), "WorldRegionAdapter", "save");
        logger.info(TransactionLog.Request.builder().body(worldRegion).build(), "request", context.getId(), "", "");
        return super.save(worldRegion)
                .doOnNext(r -> logger.info(TransactionLog.Response.builder().body(r).build(), "response save", context.getId(), "", ""))
                .switchIfEmpty(Mono.fromRunnable(() ->
                        logger.info("No se retornó ninguna respuesta del guardado", context.getId(), "", "")
                ));
    }

    @Override
    public Mono<WorldRegion> update(Context context, WorldRegion worldRegion) {
        return super.update(worldRegion);
    }

    @Override
    public Mono<WorldRegion> delete(Context context, String region, String code) {
        return super.delete(buildKey(region, code));
    }

    private Key buildKey(String partitionValue, String sortValue) {
        return Key.builder().partitionValue(partitionValue).sortValue(sortValue).build();
    }

    private Key buildKey(String partitionValue) {
        return Key.builder().partitionValue(partitionValue).build();
    }
}