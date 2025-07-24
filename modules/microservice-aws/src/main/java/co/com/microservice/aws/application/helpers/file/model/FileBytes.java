package co.com.microservice.aws.application.helpers.file.model;

import java.io.Serial;
import java.io.Serializable;

import co.com.microservice.aws.domain.model.commons.enums.TechnicalExceptionMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class FileBytes implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private byte[] bytes;
    private boolean zip;
    private TechnicalExceptionMessage technicalExceptionMessage;
}