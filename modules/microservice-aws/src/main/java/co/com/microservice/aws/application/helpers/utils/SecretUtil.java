package co.com.microservice.aws.application.helpers.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@UtilityClass
public class SecretUtil {

    public static Map<String, String> parseSecret(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, new TypeReference<>() {});
        } catch (IOException e) {
            return Collections.emptyMap();
        }
    }
}