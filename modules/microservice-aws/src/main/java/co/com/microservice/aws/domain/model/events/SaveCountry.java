package co.com.microservice.aws.domain.model.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveCountry implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private TrxData data = new TrxData();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrxData implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        private TransactionRequest transactionRequest = new TransactionRequest();
        private TransactionResponse transactionResponse = new TransactionResponse();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionRequest implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        private Headers headers = new Headers();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionResponse implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        private String statusResponse;
        private Country country = new Country();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class Country implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private String shortCode;
        private String name;
        private String description;
        private boolean status;
    }
}