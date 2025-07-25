package co.com.microservice.aws.infrastructure.output.dynamodb.operations;

import co.com.microservice.aws.infrastructure.output.dynamodb.config.DynamoDBTablesProperties;
import co.com.microservice.aws.infrastructure.output.dynamodb.config.DynamoDbTableAdapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.function.Function;

public class DynamoDBOperations<E, D>{
    protected DynamoDbEnhancedAsyncClient dbEnhancedAsyncClient;
    protected Function<E, D> fnToData;
    protected Function<D, E> fnToEntity;
    protected DynamoDbAsyncTable<D> dataTable;

    public DynamoDBOperations(DynamoDbEnhancedAsyncClient dbEnhancedAsyncClient,
                              DynamoDBTablesProperties tablesProperties, Function<E, D> fnToData,
                              Function<D, E> fnToEntity, Class<D> dataClass) {

        this.dbEnhancedAsyncClient = dbEnhancedAsyncClient;
        this.fnToData = fnToData;
        this.fnToEntity = fnToEntity;
        DynamoDbTableAdapter dynamoDbTableAdapter = dataClass.getAnnotation(DynamoDbTableAdapter.class);
        String tableName = tablesProperties.getNamesmap().get(dynamoDbTableAdapter.tableName());
        dataTable = dbEnhancedAsyncClient.table(tableName, TableSchema.fromBean(dataClass));
    }

    public Mono<E> save(E entity) {
        return Mono.just(entity).map(this::toData).flatMap(this::saveData).thenReturn(entity);
    }

    protected Mono<E> findOne(Key id) {
        return Mono.fromFuture(dataTable.getItem(id)).map(this::toEntity);
    }

    protected Mono<E> delete(Key id) {
        return deleteData(id).map(this::toEntity);
    }

    protected Mono<E> update(E entity) {
        return Mono.fromFuture(dataTable.updateItem(toData(entity))).map(this::toEntity);
    }

    protected Mono<D> saveData(D data) {
        return Mono.fromFuture(dataTable.putItem(data)).thenReturn(data);
    }

    protected Mono<D> deleteData(Key id) {
        return Mono.fromFuture(dataTable.deleteItem(id));
    }

    protected Flux<E> doQueryMany(Flux<D> query) {
        return query.map(this::toEntity);
    }

    protected D toData(E entity) {
        return fnToData.apply(entity);
    }

    protected E toEntity(D data) {
        return data != null ? fnToEntity.apply(data) : null;
    }
}