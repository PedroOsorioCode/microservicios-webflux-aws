package co.com.microservicio.aws.dynamodb.model;

import co.com.microservicio.aws.dynamodb.DynamoDbTableAdapter;
import co.com.microservicio.aws.dynamodb.config.SourceName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Data
@DynamoDbBean
@DynamoDbTableAdapter(tableName = SourceName.WORLD_REGION)
public class ModelEntityWorldRegion {

    @Getter(onMethod_ = @DynamoDbPartitionKey)
    private String primaryKey;

    @Getter(onMethod_ = @DynamoDbSortKey)
    private String sortKey;

    @Setter
    private String entityTypeKey;

    @DynamoDbSecondaryPartitionKey(indexNames = "EntityTypeIndex")
    public String getEntityTypeKey() {
        return entityTypeKey;
    }

    private String code;
    private String name;
    private String parentCode;
    private String entityName;
    private String address;
}
