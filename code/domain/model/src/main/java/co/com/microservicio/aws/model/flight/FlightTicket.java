package co.com.microservicio.aws.model.flight;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightTicket implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String documentNumber;
    private String ticket;
    private String status;
    private String flightNumber;
    private String origin;
    private String destination;
    private Double price;
    private String date;
}
