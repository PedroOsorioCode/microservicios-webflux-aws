package co.com.microservicio.aws.dynamodb.flight.model;

import co.com.microservicio.aws.dynamodb.DynamoDbTableAdapter;
import co.com.microservicio.aws.dynamodb.config.SourceName;
import lombok.Data;
import lombok.Getter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Data
@DynamoDbBean
@DynamoDbTableAdapter(tableName = SourceName.FLIGHT_TICKETS)
public class ModelEntityFlight {
    @Getter(onMethod_ = @DynamoDbPartitionKey)
    private String documentNumber;
    @Getter(onMethod_ = @DynamoDbSortKey)
    private String ticket;
    @Getter(onMethod_ = @DynamoDbSecondaryPartitionKey(indexNames = "statusIndex"))
    private String status;
    private String flightNumber;
    private String origin;
    private String destination;
    private Double price;
    private String date;
}
