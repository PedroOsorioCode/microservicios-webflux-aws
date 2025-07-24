package co.com.microservice.aws.application.usecase;

import co.com.microservice.aws.application.helpers.commons.UseCase;
import co.com.microservice.aws.domain.model.events.Event;
import co.com.microservice.aws.domain.model.events.EventData;
import co.com.microservice.aws.domain.model.rq.Context;
import co.com.microservice.aws.domain.usecase.in.commons.SentEventUseCase;
import co.com.microservice.aws.domain.usecase.out.EventPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@UseCase
@RequiredArgsConstructor
public class SentEventUseCaseImpl implements SentEventUseCase {
    private static final String INVOKER = "From-My-App";
    private final EventPort eventPort;

    @Override
    public void sentEvent(Context context, String typeEvent, Object response) {
        Mono.defer(() -> emitEvent(
                buildEvent(context, response), typeEvent, context.getId()
            ).subscribeOn(Schedulers.single())).subscribe();
    }

    private Mono<Void> emitEvent(Event<Object> event, String typeEvent, String messageId) {
        event.setId(UUID.randomUUID().toString());
        event.setType(typeEvent);
        event.setTime(LocalDateTime.now().toString());
        event.setInvoker(INVOKER);
        return eventPort.emitEvent(event, messageId);
    }

    private static Event<Object> buildEvent(Context context, Object response) {
        return Event.builder().data(EventData.builder().headers(buildHeaders(context)).data(response).build()).build();
    }

    private static Map<String, String> buildHeaders(Context context){
        Map<String, String> map = new HashMap<>();
        map.put("id", context.getId());
        map.put("ip", context.getCustomer().getIp());
        map.put("user-name", context.getCustomer().getUsername());
        map.put("user-agent", context.getCustomer().getDevice().getUserAgent());
        map.put("platform-type", context.getCustomer().getDevice().getPlatformType());

        return map;
    }
}