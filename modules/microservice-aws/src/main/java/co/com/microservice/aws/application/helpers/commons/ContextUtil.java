package co.com.microservice.aws.application.helpers.commons;

import co.com.microservice.aws.domain.model.rq.Context;
import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.Optional;

@UtilityClass
public class ContextUtil {
    private static final String EMPTY_VALUE = "";

    public static Context buildContext(Map<String, String> headers) {
        var localHeaders = HeadersUtil.clearChars(headers);
        return Context.builder().id(Optional.ofNullable(localHeaders.get("message-id")).orElse(EMPTY_VALUE))
                .customer(buildCustomer(localHeaders)).build();
    }

    private static Context.Customer buildCustomer(Map<String, String> headers) {
        return Context.Customer.builder().ip(Optional.ofNullable(headers.get("ip")).orElse(EMPTY_VALUE))
                .username(Optional.ofNullable(headers.get("user-name")).orElse(EMPTY_VALUE))
                .device(buildDevice(headers)).build();
    }

    private static Context.Device buildDevice(Map<String, String> headers) {
        return Context.Device.builder().userAgent(Optional.ofNullable(headers.get("user-agent")).orElse(EMPTY_VALUE))
                .platformType(Optional.ofNullable(headers.get("platform-type")).orElse(EMPTY_VALUE)).build();
    }
}