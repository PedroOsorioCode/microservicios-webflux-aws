package co.com.microservice.aws.infrastructure.output.mysql.entity;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(name = "parameters")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParameterEntity {
    @Id
    private Long id;
    private String name;
    private Boolean value;
    private LocalDateTime dateCreation;
}