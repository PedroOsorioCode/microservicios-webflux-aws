package co.com.microservicio.aws.usecase.flight;

import co.com.microservicio.aws.model.flight.FlightTicket;
import co.com.microservicio.aws.model.flight.gateway.FlightRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class FlightTicketUseCase {
    private final FlightRepository flightRepository;

    public Mono<FlightTicket> getAllRows(String messageId){
        return Mono.just(messageId).filter(this::isEmpty)
            .flatMap(flightRepository::getAllRows)
            .defaultIfEmpty(new FlightTicket());
    }

    private Boolean isEmpty(String messageId){
        return messageId.isEmpty();
    }
}
