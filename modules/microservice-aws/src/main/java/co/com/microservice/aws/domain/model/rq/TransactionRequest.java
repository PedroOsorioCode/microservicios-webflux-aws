package co.com.microservice.aws.domain.model.rq;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TransactionRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private transient Context context;
    private transient Object item;
    private transient List<Object> items;
}