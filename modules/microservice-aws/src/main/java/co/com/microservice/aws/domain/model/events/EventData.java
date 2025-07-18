package co.com.microservice.aws.domain.model.events;

import co.com.microservice.aws.domain.model.rq.Context;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class EventData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Context contextHeaders;
    private transient Object data;
}