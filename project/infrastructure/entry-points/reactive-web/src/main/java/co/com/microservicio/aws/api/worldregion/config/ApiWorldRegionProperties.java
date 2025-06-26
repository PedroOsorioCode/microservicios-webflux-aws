package co.com.microservicio.aws.api.worldregion.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "entries.world-region-web")
public class ApiWorldRegionProperties {
    private String pathBase;
    private String listByRegion;
    private String findOne;
    private String saveRegion;
    private String updateRegion;
    private String deleteRegion;
}
