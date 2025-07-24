package co.com.microservice.aws.application.helpers.file;

import co.com.microservice.aws.application.helpers.file.model.FileBytes;
import co.com.microservice.aws.application.helpers.file.model.FileData;
import co.com.microservice.aws.application.helpers.file.txt.TxtValidator;
import co.com.microservice.aws.application.helpers.file.zipfile.ZipValidator;
import co.com.microservice.aws.domain.model.commons.enums.TechnicalExceptionMessage;
import co.com.microservice.aws.domain.model.commons.exception.TechnicalException;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@UtilityClass
public class FlatFile {
    public static Mono<FileData> getValue(byte[] bytes) {
        if (ZipValidator.isValidZip(bytes)) {
            var fileBytes = ZipValidator.extractFileFromZip(bytes);
            if (null == fileBytes.getTechnicalExceptionMessage()) {
                return getValueTxtFile(fileBytes);
            } else {
                return Mono.error(new TechnicalException(fileBytes.getTechnicalExceptionMessage()));
            }
        } else {
            var fileBytes = FileBytes.builder().bytes(bytes).build();
            return getValueTxtFile(fileBytes);
        }
    }

    public static Mono<FileData> getValueTxtFile(FileBytes fileBytes) {
        try {
            if (TxtValidator.txtHasValidChars(fileBytes.getBytes())) {
                var fileData = FileData.builder().zip(fileBytes.isZip()).build();
                fileData.setData(new String(fileBytes.getBytes(), StandardCharsets.UTF_8));
                return Mono.just(fileData);
            } else {
                return Mono.error(new TechnicalException(TechnicalExceptionMessage.TXT_FILE_HAS_INVALID_CHARS));
            }
        } catch (TechnicalException technicalException) {
            return Mono.error(technicalException);
        }
    }
}