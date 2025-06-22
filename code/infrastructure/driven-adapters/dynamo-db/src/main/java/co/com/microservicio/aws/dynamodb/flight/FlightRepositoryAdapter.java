package co.com.microservicio.aws.dynamodb.flight;

import co.com.microservicio.aws.dynamodb.AdapterOperations;
import co.com.microservicio.aws.dynamodb.config.DynamoDBTablesProperties;
import co.com.microservicio.aws.dynamodb.flight.mapper.FlightTicketDataMapper;
import co.com.microservicio.aws.dynamodb.flight.model.ModelEntityFlight;
import co.com.microservicio.aws.model.flight.FlightTicket;
import co.com.microservicio.aws.model.flight.gateway.FlightRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;

import java.util.Map;
import java.util.function.Function;

@Component
public class FlightRepositoryAdapter extends AdapterOperations<FlightTicket, ModelEntityFlight>
        implements FlightRepository {

    public FlightRepositoryAdapter(DynamoDbEnhancedAsyncClient dbEnhancedAsyncClient,
           DynamoDBTablesProperties tablesProperties, FlightTicketDataMapper mapper) {

        super(dbEnhancedAsyncClient, tablesProperties, mapper::toData, mapper::toEntity,
                ModelEntityFlight.class);
    }

    private Key buildKey(String sortValue, String partitionValue) {
        return Key.builder().sortValue(sortValue).partitionValue(partitionValue).build();
    }

    @Override
    public Mono<FlightTicket> getAllRows(Map<String, String> param) {
        return null;
    }
}
