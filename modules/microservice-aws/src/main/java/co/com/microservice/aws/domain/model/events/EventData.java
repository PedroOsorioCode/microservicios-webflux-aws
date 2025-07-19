package co.com.microservice.aws.domain.model.events;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class EventData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private transient Map<String, String> headers;
    private transient Object data;
}