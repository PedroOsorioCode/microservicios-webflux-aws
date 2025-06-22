package co.com.microservicio.aws.usecase.flight;

import co.com.microservicio.aws.model.flight.FlightTicket;
import co.com.microservicio.aws.model.flight.gateway.FlightRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Map;

@RequiredArgsConstructor
public class FlightTicketUseCase {
    private static final String KEY_SIZE = "size";
    private static final String ATTRIBUTE_IS_REQUIRED = "The attribute '%s' is required";
    private final FlightRepository flightRepository;

    public Mono<FlightTicket> getAllRows(Map<String, String> param){
        return Mono.just(param).filter(this::isEmpty)
            .flatMap(flightRepository::getAllRows)
            .switchIfEmpty(Mono.error(new IllegalStateException(String.format(ATTRIBUTE_IS_REQUIRED, KEY_SIZE))));
    }

    private Boolean isEmpty(Map<String, String> param){
        return !param.get(KEY_SIZE).isEmpty();
    }
}
