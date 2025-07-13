package co.com.microservice.aws.infrastructure.output.postgresql.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(name = "countries", schema = "worldregion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountryEntity {
    @Id
    private Long id;
    private String shortCode;
    private String name;
    private String description;
    private boolean status;
    private LocalDateTime dateCreation;
}