package co.com.microservice.aws.application.helpers.file.model;

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
public class ZipValidatorResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean hasOnlyOneFile;
    private TechnicalExceptionMessage technicalExceptionMessage;
}