package co.com.microservice.aws.application.usecase;

import co.com.microservice.aws.application.helpers.commons.UseCase;
import co.com.microservice.aws.application.helpers.file.FlatFile;
import co.com.microservice.aws.application.helpers.file.model.FileData;
import co.com.microservice.aws.application.helpers.utils.FileStructureValidator;
import co.com.microservice.aws.domain.model.commons.exception.TechnicalException;
import co.com.microservice.aws.domain.model.events.ProccessWorldRegionFile;
import co.com.microservice.aws.domain.model.rq.Context;
import co.com.microservice.aws.domain.model.rq.TransactionRequest;
import co.com.microservice.aws.domain.usecase.in.WorldRegionUseCase;
import co.com.microservice.aws.domain.usecase.out.FileStoragePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static co.com.microservice.aws.domain.model.commons.enums.TechnicalExceptionMessage.TECHNICAL_REQUEST_ERROR;

@UseCase
@RequiredArgsConstructor
public class WorldRegionUseCaseImpl implements WorldRegionUseCase {
    private final FileStoragePort fileStoragePort;

    @Override
    public Mono<String> processFile(TransactionRequest request) {
        return Mono.just(request)
                .map(TransactionRequest::getItem)
                .flatMap(this::buildWorldRegion)
                .flatMap(infobucket -> this.getFile(infobucket, request.getContext()));
    }

    private Mono<String> getFile(ProccessWorldRegionFile.InfoBucket infoBucket, Context context){
        return Mono.just(infoBucket)
                .flatMap(ib -> fileStoragePort.getFile(context, ib.getBucketName(), ib.getPath()))
                .flatMap(FlatFile::getValue).onErrorResume(Mono::error)
                .flatMap(flatfile -> this.validateFile(flatfile, infoBucket, context))
                .doOnEach(signal -> {
                    if (signal.isOnNext()) {
                        System.out.println(Arrays.toString(signal.get()));
                    }
                    if (signal.isOnError()) {
                        signal.getThrowable().printStackTrace();
                    }
                })
                .thenReturn("Guardó");
    }

    private Mono<String[]> validateFile(FileData fileData, ProccessWorldRegionFile.InfoBucket infoBucket, Context context) {
        var fileLines = FileStructureValidator.getFileLines(fileData.getData());
        var monoReturn = Mono.just(Boolean.TRUE);
        if (fileData.isZip()) {
            var patch = FileStructureValidator.changeZipToTxt(infoBucket.getPath());
            var bucketName = FileStructureValidator.changeZipToTxt(infoBucket.getBucketName());

            monoReturn = fileStoragePort.uploadObject(context, bucketName, patch,
                    fileData.getData().getBytes());
        }

        return monoReturn.flatMap(b -> Mono.just(fileLines));
    }

    private Mono<ProccessWorldRegionFile.InfoBucket> buildWorldRegion(Object object){
        if (object instanceof ProccessWorldRegionFile.InfoBucket infoFile) {
            return Mono.just(infoFile);
        } else {
            return Mono.error(new TechnicalException(TECHNICAL_REQUEST_ERROR));
        }
    }
}