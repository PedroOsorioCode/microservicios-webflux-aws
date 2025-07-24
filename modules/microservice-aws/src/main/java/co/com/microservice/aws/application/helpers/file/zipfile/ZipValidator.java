package co.com.microservice.aws.application.helpers.file.zipfile;

import co.com.microservice.aws.application.helpers.file.mime.MimeDetect;
import co.com.microservice.aws.application.helpers.file.mime.MimeTypes;
import co.com.microservice.aws.application.helpers.file.model.FileBytes;
import co.com.microservice.aws.application.helpers.file.model.ZipValidatorResult;
import co.com.microservice.aws.domain.model.commons.enums.TechnicalExceptionMessage;
import lombok.experimental.UtilityClass;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@UtilityClass
public class ZipValidator {
    private static final int BUFFER_SIZE = 4;

    public static FileBytes extractFileFromZip(byte[] bytes) {
        var returnValidation = isValidZipOnlyOneFile(bytes);

        var fileBytes = FileBytes.builder().bytes(bytes)
                .technicalExceptionMessage(returnValidation.getTechnicalExceptionMessage())
                .build();

        if (null != returnValidation.getTechnicalExceptionMessage()) {
            return fileBytes;
        }

        if (returnValidation.isHasOnlyOneFile()) {
            getOnlyOneFile(fileBytes);
        } else {
            fileBytes.setTechnicalExceptionMessage(TechnicalExceptionMessage.ZIP_FILE_HASNT_ONLY_ONE_FILE);
        }
        return fileBytes;
    }

    public static ZipValidatorResult isValidZipOnlyOneFile(byte[] bytes) {
        var result = ZipValidatorResult.builder().build();

        if (!isValidZip(bytes)) {
            return result;
        }

        try (var zipInput = new ZipArchiveInputStream(new ByteArrayInputStream(bytes))) {
            int entryCount = 0;
            while (zipInput.getNextEntry() != null) {
                entryCount++;
            }
            result.setHasOnlyOneFile(entryCount == 1);
        } catch (IOException e) {
            result.setTechnicalExceptionMessage(TechnicalExceptionMessage.ZIP_FILE_IS_WRONG);
        }
        return result;
    }

    public static boolean isValidZip(byte[] bytes) {
        var returnValidation = false;
        var mime = MimeDetect.getMimeType(bytes);
        if (MimeTypes.ZIP.equals(mime)) {
            returnValidation = true;
        }
        return returnValidation;
    }

    public static void getOnlyOneFile(FileBytes fileBytes) {
        fileBytes.setZip(true);
        try (var inputStream = new ByteArrayInputStream(fileBytes.getBytes());
             var zipInput = new ZipArchiveInputStream(inputStream);
             var outputStream = new ByteArrayOutputStream()) {

            if (zipInput.getNextEntry() != null) {
                var buffer = new byte[BUFFER_SIZE];
                int length;
                while ((length = zipInput.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }
                fileBytes.setBytes(outputStream.toByteArray());
            }

        } catch (IOException e) {
            fileBytes.setTechnicalExceptionMessage(TechnicalExceptionMessage.ZIP_FILE_IS_WRONG);
        }
    }
}