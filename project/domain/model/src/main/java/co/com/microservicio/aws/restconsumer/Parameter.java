package co.com.microservicio.aws.restconsumer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Parameter implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private String value;
    private String status;
}
