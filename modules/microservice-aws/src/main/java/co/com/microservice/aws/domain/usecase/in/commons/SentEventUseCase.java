package co.com.microservice.aws.domain.usecase.in.commons;

import co.com.microservice.aws.domain.model.rq.Context;

public interface SentEventUseCase {
    void sentEvent(Context context, String typeEvent, Object response);
}