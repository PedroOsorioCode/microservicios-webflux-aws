package co.com.microservice.aws.domain.model.events;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Headers implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonAlias({ "message-id" })
    private String messageId;
    private String ip;
    @JsonAlias({ "user-name" })
    private String username;
    @JsonAlias({ "user-agent" })
    private String userAgent;
    @JsonAlias({ "platform-type" })
    private String platformType;
}