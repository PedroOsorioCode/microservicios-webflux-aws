package co.com.microservicio.aws.dynamodb;

import co.com.microservicio.aws.dynamodb.config.DynamoDBTablesProperties;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.function.Function;

public class AdapterOperations<E, D> extends DynamoDBOperations<E, D> {

    public AdapterOperations(DynamoDbEnhancedAsyncClient dbEnhancedAsyncClient,
                             DynamoDBTablesProperties tablesProperties,
                             Function<E, D> fnToData, Function<D, E> fnToEntity,
                             Class<D> dataClass) {

        super(dbEnhancedAsyncClient, tablesProperties, fnToData, fnToEntity, dataClass);
    }

    @Override
    protected E toEntity(D data) {
        return fnToEntity.apply(data);
    }

    protected Flux<E> findByQuery(QueryEnhancedRequest queryRequest) {
        return Mono.just(dataTable)
                .flatMap(index -> Mono.from(index.query(queryRequest)))
                .flatMapMany(page -> doQueryMany(Flux.fromIterable(page.items())))
                .onErrorResume(err -> Flux.empty());
    }

}
