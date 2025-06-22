package co.com.microservicio.aws.dynamodb;

import java.util.function.Function;

import co.com.microservicio.aws.dynamodb.config.DynamoDBTablesProperties;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

public class AdapterOperations<E, D> {
    protected DynamoDbEnhancedAsyncClient dbEnhancedAsyncClient;
    protected Function<E, D> fnToData;
    protected Function<D, E> fnToEntity;
    protected DynamoDbAsyncTable<D> dataTable;

    public AdapterOperations(DynamoDbEnhancedAsyncClient dbEnhancedAsyncClient,
                             DynamoDBTablesProperties tablesProperties, Function<E, D> fnToData, Function<D, E> fnToEntity,
                             Class<D> dataClass) {
        this.dbEnhancedAsyncClient = dbEnhancedAsyncClient;
        this.fnToData = fnToData;
        this.fnToEntity = fnToEntity;
        var dynamoDbTableAdapter = dataClass.getAnnotation(DynamoDbTableAdapter.class);
        var tableName = tablesProperties.getNamesmap().get(dynamoDbTableAdapter.tableName());
        dataTable = dbEnhancedAsyncClient.table(tableName, TableSchema.fromBean(dataClass));
    }

    protected D toData(E entity) {
        return fnToData.apply(entity);
    }

    protected E toEntity(D data) {
        return fnToEntity.apply(data);
    }

    protected Mono<E> findOne(Key id) {
        return Mono.fromFuture(dataTable.getItem(id)).map(this::toEntity);
    }

    protected Mono<E> update(E entity) {
        return Mono.fromFuture(dataTable.updateItem(toData(entity))).map(this::toEntity);
    }
}

