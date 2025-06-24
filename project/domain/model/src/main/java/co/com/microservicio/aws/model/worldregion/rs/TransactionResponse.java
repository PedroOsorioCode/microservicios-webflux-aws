package co.com.microservicio.aws.model.worldregion.rs;

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
    private int size;
    private List<WorldRegionResponse> response;
}
