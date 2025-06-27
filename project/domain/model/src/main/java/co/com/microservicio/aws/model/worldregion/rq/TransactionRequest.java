package co.com.microservicio.aws.model.worldregion.rq;

import co.com.microservicio.aws.model.worldregion.WorldRegion;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TransactionRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private transient Context context;
    private transient Param param;
    private transient WorldRegion item;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class Param {
        private String placeType;
        private String place;
        private String code;
    }
}
