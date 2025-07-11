package co.com.microservice.aws.infrastructure.output.postgresql.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(name = "community_contry")
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