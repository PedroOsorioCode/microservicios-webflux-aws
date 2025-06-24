package co.com.microservicio.aws.dynamodb;

import co.com.microservicio.aws.dynamodb.config.DynamoDBTablesProperties;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class AdapterOperations<E, D> extends DynamoDBOperations<E, D> {

    public AdapterOperations(DynamoDbEnhancedAsyncClient dbEnhancedAsyncClient,
                             DynamoDBTablesProperties tablesProperties,
                             Function<E, D> fnToData, Function<D, E> fnToEntity,
                             Class<D> dataClass) {

        super(dbEnhancedAsyncClient, tablesProperties, fnToData, fnToEntity, dataClass);
    }

    protected D toData(E entity) {
        return fnToData.apply(entity);
    }

    protected E toEntity(D data) {
        return fnToEntity.apply(data);
    }

    protected Flux<E> toEntityList(List<D> dataList) {
        return Flux.fromIterable(dataList).map(fnToEntity);
    }

    protected Mono<E> findOne(Key id) {
        return Mono.fromFuture(dataTable.getItem(id)).map(this::toEntity);
    }

    protected Flux<E> findByIndexWithQuery(String indexName, QueryEnhancedRequest queryRequest) {
        Mono<Page<D>> pageQuery = Mono.just(dataTable)
                .map(table -> table.index(indexName))
                .map(index -> index.query(queryRequest))
                .flatMap(Mono::from);

        Flux<E> result = doQueryMany(pageQuery.map(Page::items)
                .flatMapMany(Flux::fromIterable));

        return pageQuery
                .filter(page -> Objects.nonNull(page.lastEvaluatedKey()))
                .flatMapMany(dPage -> result.concatWith(findByIndexWithQuery(indexName,
                        queryRequest.toBuilder().exclusiveStartKey(dPage.lastEvaluatedKey()).build())))
                .switchIfEmpty(result);
    }

    protected Mono<E> update(E entity) {
        return Mono.fromFuture(dataTable.updateItem(toData(entity))).map(this::toEntity);
    }
}
