package co.com.microservice.aws.infrastructure.output.dynamodb.model;

import co.com.microservice.aws.infrastructure.output.dynamodb.config.DynamoDbTableAdapter;
import lombok.Data;
import lombok.Getter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@Data
@DynamoDbBean
@DynamoDbTableAdapter(tableName = SourceName.WORLD_REGION)
public class ModelEntityWorldRegion {
    @Getter(onMethod_ = @DynamoDbPartitionKey)
    private String region;

    @Getter(onMethod_ = @DynamoDbSortKey)
    private String code;

    private String name;
    private String codeRegion;
    private String creationDate;
}