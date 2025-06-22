package co.com.microservicio.aws.model.flight.gateway;

import co.com.microservicio.aws.model.flight.FlightTicket;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface FlightRepository {
    Mono<FlightTicket> getAllRows(Map<String, String> param);
}
