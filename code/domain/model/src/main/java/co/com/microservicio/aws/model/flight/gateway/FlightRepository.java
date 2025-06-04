package co.com.microservicio.aws.model.flight.gateway;

import co.com.microservicio.aws.model.flight.FlightTicket;
import reactor.core.publisher.Mono;

public interface FlightRepository {
    Mono<FlightTicket> getAllRows(String messageId);
}
