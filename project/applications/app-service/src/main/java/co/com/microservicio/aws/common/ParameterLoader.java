package co.com.microservicio.aws.common;

import co.com.microservicio.aws.usecase.restconsumer.RestParameterUseCase;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
@AllArgsConstructor
public class ParameterLoader {
    private RestParameterUseCase parameterUseCase;

    @EventListener(ApplicationReadyEvent.class)
    public void saveCacheProcessFileContingency() {
        parameterUseCase.saveParameterAuditOnSave().subscribe();
    }
    @EventListener(ApplicationReadyEvent.class)
    public void saveCacheSendFileWas() {
        parameterUseCase.saveParameterAuditOnUpdate().subscribe();
    }
}
