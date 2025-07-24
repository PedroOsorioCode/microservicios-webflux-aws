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
public class ProccessWorldRegionFile implements Serializable {
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
        private InfoBucket response = new InfoBucket();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class InfoBucket implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private String fileName;
        private String bucketName;
        private String path;
    }
}