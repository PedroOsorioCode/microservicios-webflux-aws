package co.com.microservice.aws.application.helpers.commons;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import lombok.experimental.UtilityClass;

@UtilityClass
public class HeadersUtil {

    private static final String CHARS_TO_CLEAR = "<>(;|'";
    private static final String REGEXP_CHARS_TO_CLEAR = "[" + CHARS_TO_CLEAR + "]";
    private static final Pattern PATTERN_CHARS_TO_CLEAR = Pattern.compile(REGEXP_CHARS_TO_CLEAR);

    public static Map<String, String> clearChars(Map<String, String> headers) {
        var localHeaders = new LinkedHashMap<String, String>();
        if (null != headers && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                localHeaders.put(entry.getKey(), PATTERN_CHARS_TO_CLEAR.matcher(entry.getValue()).replaceAll(" "));
            }
        }
        return localHeaders;
    }
}