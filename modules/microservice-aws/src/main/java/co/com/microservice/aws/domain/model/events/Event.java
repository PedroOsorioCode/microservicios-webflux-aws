package co.com.microservice.aws.domain.model.events;

import java.io.Serial;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Event<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String type;
    private String specVersion;
    private String source;
    private String id;
    private String time;
    protected String invoker;
    private String dataContentType;

    protected transient T data;

    public String getEventId() {
        return id.concat("-".concat(type));
    }

    public Event<T> complete(String source, String specVersion, String dataContentType) {
        this.setSource(source);
        this.setSpecVersion(specVersion);
        this.setDataContentType(dataContentType);
        return this;
    }
}