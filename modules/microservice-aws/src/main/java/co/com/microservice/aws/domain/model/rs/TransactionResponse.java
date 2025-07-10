package co.com.microservice.aws.domain.model.rs;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TransactionResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String message;
    private String size;
    private List<Object> response;
}