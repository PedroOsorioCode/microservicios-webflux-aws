package co.com.microservice.aws.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Country implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String shortCode;
    private String name;
    private String description;
    private boolean status;
    private LocalDateTime dateCreation;
}