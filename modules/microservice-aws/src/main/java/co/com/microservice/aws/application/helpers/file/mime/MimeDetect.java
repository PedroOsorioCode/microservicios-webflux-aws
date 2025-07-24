package co.com.microservice.aws.application.helpers.file.mime;

import org.apache.tika.Tika;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MimeDetect {
    public static String getMimeType(byte[] fileBytes) {
        var tika = new Tika();
        return tika.detect(fileBytes);
    }
}