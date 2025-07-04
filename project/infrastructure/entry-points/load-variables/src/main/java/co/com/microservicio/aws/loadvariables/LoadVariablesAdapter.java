package co.com.microservicio.aws.loadvariables;

import co.com.microservicio.aws.loadvariables.properties.AuditConfigProperties;
import co.com.microservicio.aws.variables.gateway.LoadVariablesGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoadVariablesAdapter implements LoadVariablesGateway {
    private final AuditConfigProperties auditConfigProperties;

    @Override
    public boolean isAuditOnList() {
        return auditConfigProperties.isAuditOnList();
    }
}