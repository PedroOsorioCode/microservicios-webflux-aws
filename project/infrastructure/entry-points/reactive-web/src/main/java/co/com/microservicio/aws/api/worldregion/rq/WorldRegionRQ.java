package co.com.microservicio.aws.api.worldregion.rq;

import co.com.microservicio.aws.utils.validator.gateways.ValidCodeRegion;
import co.com.microservicio.aws.utils.validator.gateways.ValidName;
import co.com.microservicio.aws.utils.validator.gateways.ValidRegion;
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
public class WorldRegionRQ implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @ValidRegion
    private String region;
    private String code;
    @ValidName
    private String name;
    @ValidCodeRegion
    private String codeRegion;
    private String creationDate;
}
