package co.com.microservice.aws.application.usecase;

import co.com.microservice.aws.application.helpers.commons.UseCase;
import co.com.microservice.aws.application.helpers.file.FlatFile;
import co.com.microservice.aws.application.helpers.file.model.FileData;
import co.com.microservice.aws.application.helpers.utils.FileStructureValidator;
import co.com.microservice.aws.domain.model.WorldRegion;
import co.com.microservice.aws.domain.model.commons.exception.TechnicalException;
import co.com.microservice.aws.domain.model.commons.util.ResponseMessageConstant;
import co.com.microservice.aws.domain.model.events.ProccessWorldRegionFile;
import co.com.microservice.aws.domain.model.rq.Context;
import co.com.microservice.aws.domain.model.rq.TransactionRequest;
import co.com.microservice.aws.domain.usecase.in.WorldRegionUseCase;
import co.com.microservice.aws.domain.usecase.out.FileStoragePort;
import co.com.microservice.aws.domain.usecase.out.WorldRegionPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static co.com.microservice.aws.domain.model.commons.enums.TechnicalExceptionMessage.TECHNICAL_REQUEST_ERROR;

@UseCase
@RequiredArgsConstructor
public class WorldRegionUseCaseImpl implements WorldRegionUseCase {
    private final FileStoragePort fileStoragePort;
    private final WorldRegionPort worldRegionPort;

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
            .flatMap(flatfile -> this.save(flatfile, context))
            .flatMap(msg -> fileStoragePort.deleteObject(context, infoBucket.getBucketName(), infoBucket.getPath()).thenReturn(msg))
            .onErrorResume(Mono::error);
    }

    private Mono<String[]> validateFile(FileData fileData, ProccessWorldRegionFile.InfoBucket infoBucket, Context context) {
        var fileLines = FileStructureValidator.getFileLines(fileData.getData());
        var monoReturn = Mono.just(Boolean.TRUE);
        if (fileData.isZip()) {
            var path = FileStructureValidator.changeZipToTxt(infoBucket.getPath());
            var bucketName = infoBucket.getBucketName();

            monoReturn = fileStoragePort.uploadObject(context, bucketName, path,
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

    private Mono<String> save(String[] flatfile, Context context){
        return Flux.fromArray(flatfile)
            .map(String::trim)
            .filter(line -> !line.isBlank())
            .map(line -> {
                String[] parts = line.split(";");
                return WorldRegion.builder().region(parts[0])
                        .code(parts[1]).name(parts[2])
                        .codeRegion(parts[3]).creationDate(LocalDateTime.now().toString())
                        .build();
            })
            .flatMap(worldRegion -> worldRegionPort.save(context, worldRegion))
            .then(Mono.just(ResponseMessageConstant.MSG_SAVED_SUCCESS));
    }
}