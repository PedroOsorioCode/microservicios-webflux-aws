package co.com.microservicio.aws.api.commons;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.experimental.UtilityClass;

import static org.springdoc.core.fn.builders.parameter.Builder.parameterBuilder;
import static org.springdoc.core.fn.builders.schema.Builder.schemaBuilder;

@UtilityClass
public class HeaderOpenApi {
    public final String TEXT = "";

    public static org.springdoc.core.fn.builders.parameter.Builder getHeaderMessageId() {
        return parameterBuilder().in(ParameterIn.HEADER).name("message-id")
                .description("ID for transaction traceability. Must be provided " + "by the front in UUID format")
                .schema(schemaBuilder().type(TEXT).example("8348c30c-1296-4882-84b8-d7306205ce26")).required(true);
    }

    public static org.springdoc.core.fn.builders.parameter.Builder getHeaderSessionTracker() {
        return parameterBuilder().in(ParameterIn.HEADER).name("session-tracker")
                .description("ID for session traceability. Must be provided by the front in UUID format")
                .schema(schemaBuilder().type(TEXT).example("c4e6bd04-5149-11e7-b114-b2f933d5fe81")).required(true);
    }

    public static org.springdoc.core.fn.builders.parameter.Builder getHeaderRequestTimestamp() {
        return parameterBuilder().in(ParameterIn.HEADER).name("request-timestamp")
                .description("Date and Time the request is made")
                .schema(schemaBuilder().type(TEXT).example("2023-03-14 19:30:59:000")).required(true);
    }

    public static org.springdoc.core.fn.builders.parameter.Builder getHeaderIP() {
        return parameterBuilder().in(ParameterIn.HEADER).name("x-forwarded-for")
                .description("IP of the device in which the request was generated")
                .schema(schemaBuilder().type(TEXT).example("127.0.0.1")).required(true);
    }

    public static org.springdoc.core.fn.builders.parameter.Builder getHeaderUserAgent() {
        return parameterBuilder().in(ParameterIn.HEADER).name("user-agent")
                .description("User agent for device identification")
                .schema(schemaBuilder().type(TEXT).example(
                        "{\"device\":\"iPhone\",\"os\":\"CPU iPhone OS 13_5_1\"," + "\"browser\":\"Version/13.1.1\"}"))
                .required(true);
    }

    public static org.springdoc.core.fn.builders.parameter.Builder getHeaderPlatformType() {
        return parameterBuilder().in(ParameterIn.HEADER).name("platform-type")
                .description("Type of platform where the request comes from (web - mobile)")
                .schema(schemaBuilder().type(TEXT).example("mobile")).required(true);
    }

    public static org.springdoc.core.fn.builders.parameter.Builder getHeaderDocumentId() {
        return parameterBuilder().in(ParameterIn.HEADER).name("document-number")
                .description("Identity document number of a client")
                .schema(schemaBuilder().type(TEXT).example("210195722")).required(true);
    }

    public static org.springdoc.core.fn.builders.parameter.Builder getHeaderDocumentType() {
        return parameterBuilder().in(ParameterIn.HEADER).name("document-type")
                .description("Type of identity document of a client").schema(schemaBuilder().type(TEXT).example("CC"))
                .required(true);
    }

    public static org.springdoc.core.fn.builders.parameter.Builder getHeaderContentType() {
        return parameterBuilder().in(ParameterIn.HEADER).name("Content-Type")
                .description("Type of content sent in the request")
                .schema(schemaBuilder().type(TEXT).example("application/json")).required(true);
    }

}