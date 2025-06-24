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

    private String primaryKey;
    private String sortKey;
    private String entityType;
    private String code;
    private String name;
    private String parentCode;
    private String entityTypeKey;
    private String entityName;
    private String address;
}
