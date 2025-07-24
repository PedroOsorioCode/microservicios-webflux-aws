package co.com.microservice.aws.application.helpers.file.model;

import java.io.Serial;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class FileData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String data;
    private boolean zip;
}