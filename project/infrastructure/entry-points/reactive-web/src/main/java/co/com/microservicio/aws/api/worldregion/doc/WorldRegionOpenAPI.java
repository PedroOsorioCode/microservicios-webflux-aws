package co.com.microservicio.aws.api.worldregion.doc;

import lombok.experimental.UtilityClass;
import org.springdoc.core.fn.builders.operation.Builder;

import java.util.function.Consumer;

import static org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder;
import static org.springdoc.core.fn.builders.content.Builder.contentBuilder;
import static org.springdoc.core.fn.builders.schema.Builder.schemaBuilder;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@UtilityClass
public class WorldRegionOpenAPI {

    private static final String OPERATION_ID = "Greet";
    private static final String DESCRIPTION = "Retrieve information of a world regions";
    private static final String DESCRIPTION_OK = "When the response has status 200";
    private static final String DESCRIPTION_CONFLICT = "When the request fails";
    private static final String DESCRIPTION_ERROR = "Internal server error";
    private static final String TAG = "Payments";

    public static Consumer<Builder> greetRoute() {
        return ops -> ops
                .operationId(OPERATION_ID)
                .description(DESCRIPTION)
                .tag(TAG)
                .summary(OPERATION_ID)
                .response(responseOk())
                .response(responseBusiness())
                .response(responseError())
                .response(responseNotFound())
                .response(responseBadRequest());
    }

    public static org.springdoc.core.fn.builders.apiresponse.Builder responseOk(){
        return responseBuilder().
                responseCode(String.valueOf(OK.value()))
                .description(DESCRIPTION_OK)
                .content(contentBuilder()
                        .mediaType(APPLICATION_JSON.toString())
                        .schema(schemaBuilder()
                                .implementation(String.class)));
    }

    public static org.springdoc.core.fn.builders.apiresponse.Builder responseBusiness(){
        return responseBuilder()
                .responseCode(String.valueOf(CONFLICT.value()))
                .description(DESCRIPTION_CONFLICT)
                .implementation(Error.class);
    }

    public static org.springdoc.core.fn.builders.apiresponse.Builder responseError(){
        return responseBuilder()
                .responseCode(String.valueOf(INTERNAL_SERVER_ERROR.value()))
                .description(DESCRIPTION_ERROR)
                .implementation(Error.class);
    }

    public static org.springdoc.core.fn.builders.apiresponse.Builder responseNotFound(){
        return responseBuilder()
                .responseCode(String.valueOf(NOT_FOUND.value()))
                .description(NOT_FOUND.getReasonPhrase())
                .implementation(Error.class);
    }

    public static org.springdoc.core.fn.builders.apiresponse.Builder responseBadRequest() {
        return responseBuilder()
                .responseCode(String.valueOf(BAD_REQUEST.value()))
                .description(BAD_REQUEST.getReasonPhrase())
                .implementation(Error.class);
    }

}

