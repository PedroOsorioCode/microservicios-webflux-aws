package co.com.microservicio.aws.dynamodb.flight.mapper;

import co.com.microservicio.aws.dynamodb.flight.model.ModelEntityFlight;
import co.com.microservicio.aws.model.flight.FlightTicket;

import java.util.List;

import co.com.microservicio.aws.model.flight.ValidationResponse;
import org.apache.logging.log4j.util.Strings;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FlightTicketDataMapper {
    ModelEntityFlight toData(FlightTicket flightTicket);

    @Mapping(target = "errors", source = "errors", qualifiedByName = "mapErrors")
    FlightTicket toEntity(ModelEntityFlight modelEntityFlight);

    @SneakyThrows
    default String getErrors(List<ValidationResponse> errors) {
        var mapper = new ObjectMapper();
        return errors != null ? mapper.writeValueAsString(errors) : null;
    }

    @SneakyThrows
    @Named("mapErrors")
    default List<ValidationResponse> getErrors(String errors) {
        var mapper = new ObjectMapper();
        return Strings.isBlank(errors) ? List.of() : mapper.readValue(errors, new TypeReference<>() {
        });
    }
}
