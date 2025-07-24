package co.com.microservice.aws.infrastructure.output.s3repository.operations;

import co.com.microservice.aws.domain.model.commons.enums.TechnicalExceptionMessage;
import co.com.microservice.aws.domain.model.commons.exception.TechnicalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.BytesWrapper;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@RequiredArgsConstructor
public class S3Operations {
    private final S3AsyncClient s3AsyncClient;

    public Mono<Boolean> uploadObject(String bucketName, String objectKey, byte[] fileContent) {
        return Mono
                .fromFuture(s3AsyncClient.putObject(configurePutObject(bucketName, objectKey),
                        AsyncRequestBody.fromBytes(fileContent)))
                .map(response -> response.sdkHttpResponse().isSuccessful());
    }

    public Mono<byte[]> getObject(String bucketName, String objectKey) {
        return Mono
                .fromFuture(
                        s3AsyncClient.getObject(GetObjectRequest.builder().key(objectKey).bucket(bucketName).build(),
                                AsyncResponseTransformer.toBytes()))
                .map(BytesWrapper::asByteArray).onErrorMap(Exception.class,
                        exception -> new TechnicalException(exception, TechnicalExceptionMessage.TECHNICAL_GETTING_S3_OBJECT_FAILED));
    }

    public Mono<Boolean> deleteObject(String bucketName, String objectKey) {
        return Mono
                .fromFuture(s3AsyncClient
                        .deleteObject(DeleteObjectRequest.builder().key(objectKey).bucket(bucketName).build()))
                .map(response -> response.sdkHttpResponse().isSuccessful());
    }

    private PutObjectRequest configurePutObject(String bucketName, String objectKey) {
        return PutObjectRequest.builder().bucket(bucketName).key(objectKey).build();
    }

}