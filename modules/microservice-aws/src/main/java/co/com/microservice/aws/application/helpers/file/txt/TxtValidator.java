package co.com.microservice.aws.application.helpers.file.txt;

import co.com.microservice.aws.application.helpers.file.mime.MimeDetect;
import co.com.microservice.aws.application.helpers.file.mime.MimeTypes;
import co.com.microservice.aws.domain.model.commons.enums.TechnicalExceptionMessage;
import co.com.microservice.aws.domain.model.commons.exception.TechnicalException;
import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

@UtilityClass
public class TxtValidator {
    public static boolean txtHasValidChars(byte[] bytes) {
        var returnValidation = false;
        if (isValidTxt(bytes)) {
            returnValidation = hasValidChars(bytes);
        } else {
            throw new TechnicalException(TechnicalExceptionMessage.FILE_ISNT_TXT);
        }
        return returnValidation;
    }

    public static boolean isValidTxt(byte[] bytes) {
        var returnValidation = false;
        var mime = MimeDetect.getMimeType(bytes);
        if (MimeTypes.TEXT_PLAIN.equals(mime)) {
            returnValidation = true;
        }
        return returnValidation;
    }

    private static boolean hasValidChars(byte[] bytes) {
        var regex = "[´¦¢£¥©§¶`µ\\\\×°¡¿]";
        var pattern = Pattern.compile(regex);
        var content = new String(bytes, StandardCharsets.UTF_8);
        var matcher = pattern.matcher(content);
        return !matcher.find();
    }
}