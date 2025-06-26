package co.com.microservicio.aws.model.worldregion;

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
public class WorldRegion implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String region;
    private String code;
    private String name;
    private String codeRegion;
    private String creationDate;
}
