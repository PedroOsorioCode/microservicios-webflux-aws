package co.com.microservice.aws.domain.usecase.out;

import co.com.microservice.aws.domain.model.rq.Context;
import reactor.core.publisher.Mono;
public interface FileStoragePort {
    Mono<Boolean> uploadObject(Context context, String bucketName, String objectKey, byte[] file);
    Mono<Boolean> deleteObject(Context context, String bucketName, String objectKey);
    Mono<byte[]> getFile(Context context, String bucketName, String objectPath);
}