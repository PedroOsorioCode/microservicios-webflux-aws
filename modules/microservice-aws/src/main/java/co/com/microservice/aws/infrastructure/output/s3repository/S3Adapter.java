package co.com.microservice.aws.infrastructure.output.s3repository;

import co.com.microservice.aws.application.helpers.logs.LoggerBuilder;
import co.com.microservice.aws.domain.model.rq.Context;
import co.com.microservice.aws.domain.usecase.out.FileStoragePort;
import co.com.microservice.aws.infrastructure.output.s3repository.operations.S3Operations;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class S3Adapter implements FileStoragePort {
    private static final String NAME_CLASS = S3Adapter.class.getName();
    private final S3Operations s3Operations;
    private final LoggerBuilder logger;

    @Override
    public Mono<Boolean> uploadObject(Context context, String bucketName, String objectKey, byte[] file) {
        return s3Operations.uploadObject(bucketName, objectKey, file).doOnSuccess(
                success -> logger.info("uploadObject success", context.getId(), "uploadObject", NAME_CLASS))
                .doOnError(logger::error);
    }

    @Override
    public Mono<Boolean> deleteObject(Context context, String bucketName, String objectKey) {
        return s3Operations.deleteObject(bucketName, objectKey)
                .doOnSuccess(success -> logger.info("deleteObject success", context.getId(), "deleteObject", NAME_CLASS))
                .doOnError(logger::error);
    }

    @Override
    public Mono<byte[]> getFile(Context context, String bucketName, String objectPath) {
        return s3Operations.getObject(bucketName, objectPath)
                .doOnSuccess(success -> logger.info("getFile success", context.getId(), "getFile", NAME_CLASS))
                .doOnError(logger::error);
    }
}