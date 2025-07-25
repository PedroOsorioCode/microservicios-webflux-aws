package co.com.microservice.aws.domain.model;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Setter
@Getter
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