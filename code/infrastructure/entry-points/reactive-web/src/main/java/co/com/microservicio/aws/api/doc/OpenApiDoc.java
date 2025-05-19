package co.com.microservicio.aws.api.doc;

import co.com.microservicio.aws.api.commons.HeaderOpenApi;
import org.springdoc.core.fn.builders.operation.Builder;
import java.util.function.Consumer;
import static org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder;

public class OpenApiDoc {
    public static final String SUCCESSFUL = "200";
    public static final String SUCCESSFUL_DESCRIPTION = "successful operation";
    public static final String BAD_REQUEST = "400";
    public static final String NOT_FOUND = "404";
    public static final String CONFLICT = "409";
    public static final String INTERNAL_SERVER_ERROR = "500";

    public static Consumer<Builder> executeListDataExampleOpenApi() {
        return ops -> ops.tag("List data example").operationId("/list-data-example")
                .description("List data example").parameter(HeaderOpenApi.getHeaderMessageId())
                .parameter(HeaderOpenApi.getHeaderSessionTracker())
                .parameter(HeaderOpenApi.getHeaderRequestTimestamp()).parameter(HeaderOpenApi.getHeaderIP())
                .parameter(HeaderOpenApi.getHeaderUserAgent()).parameter(HeaderOpenApi.getHeaderPlatformType())
                .parameter(HeaderOpenApi.getHeaderDocumentId())
                .parameter(HeaderOpenApi.getHeaderDocumentType()).parameter(HeaderOpenApi.getHeaderContentType())
                .response(responseBuilder().responseCode(SUCCESSFUL).description(SUCCESSFUL_DESCRIPTION)
                        .implementation(Object.class))
                .response(getTechnicalError()).response(getBusinessError()).response(getDefaultError()).build();
    }

    public static org.springdoc.core.fn.builders.apiresponse.Builder getTechnicalError() {
        return responseBuilder().responseCode(INTERNAL_SERVER_ERROR).description("Technical error")
                .implementation(String.class);
    }

    public static org.springdoc.core.fn.builders.apiresponse.Builder getBusinessError() {
        return responseBuilder().responseCode(CONFLICT).description("Business error")
                .implementation(String.class);
    }

    public static org.springdoc.core.fn.builders.apiresponse.Builder getDefaultError() {
        return responseBuilder().responseCode(BAD_REQUEST).description("Default error")
                .implementation(String.class);
    }

    public static org.springdoc.core.fn.builders.apiresponse.Builder getNotFoundError() {
        return responseBuilder().responseCode(NOT_FOUND).description("Not Found error")
                .implementation(String.class);
    }
}
