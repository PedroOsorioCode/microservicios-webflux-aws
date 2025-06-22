package co.com.microservicio.aws.model.flight;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ValidationResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean valid;
    private String reason;
    private String validationCode;
}
