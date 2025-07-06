package co.com.microservicio.aws.usecase.sentevent;

import co.com.microservicio.aws.event.gateway.EventGateway;
import co.com.microservicio.aws.model.worldregion.rq.TransactionRequest;
import co.com.microservicio.aws.variables.gateway.LoadVariablesGateway;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SentEventUseCase {
    private final LoadVariablesGateway loadVariablesGateway;
    private final EventGateway eventGateway;

    public void sendAuditList(TransactionRequest request){
        if (loadVariablesGateway.isAuditOnList()){
            eventGateway.emitEvent(request, "audit on list");
        }
    }

    public void sendAuditSave(TransactionRequest request, Boolean auditOnSave){
        if (Boolean.TRUE.equals(auditOnSave)){
            eventGateway.emitEvent(request, "audit on save");
        }
    }
}