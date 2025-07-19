package co.com.microservice.aws.infrastructure.input.listenevent.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import org.reactivecommons.api.domain.DomainEvent;

@UtilityClass
public class EventData {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> T getValueData(DomainEvent<Object> event, Class<T> clazz) {
        return objectMapper.convertValue(event.getData(), clazz);
    }
}