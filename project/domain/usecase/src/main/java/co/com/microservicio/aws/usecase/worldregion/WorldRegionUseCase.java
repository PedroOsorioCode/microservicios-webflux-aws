package co.com.microservicio.aws.usecase.worldregion;

import co.com.microservicio.aws.model.worldregion.WorldRegion;
import co.com.microservicio.aws.model.worldregion.gateway.WorldRegionRepository;
import co.com.microservicio.aws.model.worldregion.rq.TransactionRequest;
import co.com.microservicio.aws.model.worldregion.rq.Context;
import co.com.microservicio.aws.model.worldregion.rq.Customer;
import co.com.microservicio.aws.model.worldregion.rs.TransactionResponse;
import co.com.microservicio.aws.model.worldregion.rs.WorldRegionResponse;
import co.com.microservicio.aws.model.worldregion.util.WorldRegionType;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class WorldRegionUseCase {
    private static final String KEY_USER_NAME = "user-name";
    private static final String ATTRIBUTE_IS_REQUIRED = "The attribute '%s' is required";

    private final WorldRegionRepository regionRepository;

    public Mono<TransactionResponse> listAllCountries(TransactionRequest request){
        return Mono.just(request)
            .filter(this::userIsRequired)
            .flatMap(req -> regionRepository.findByEntityType(WorldRegionType.COUNTRY)
                    .collectList().flatMap(this::buildResponse)
            ).switchIfEmpty(Mono.error(new IllegalStateException(
                    String.format(ATTRIBUTE_IS_REQUIRED, KEY_USER_NAME))));
    }

    private Boolean userIsRequired(TransactionRequest request){
        return Optional.ofNullable(request)
                .map(TransactionRequest::getContext)
                .map(Context::getCustomer)
                .map(Customer::getUsername)
                .filter(username -> !username.isEmpty())
                .isPresent();
    }

    private Mono<TransactionResponse> buildResponse(List<WorldRegion> worldRegions){
        var simplifiedList = worldRegions.stream()
            .map(wr -> WorldRegionResponse.builder()
                .code(wr.getCode())
                .name(wr.getName())
                .build())
            .toList();

        TransactionResponse response = TransactionResponse.builder()
                .message("countries listed successfull")
                .size(worldRegions.size())
                .response(simplifiedList)
                .build();

        return Mono.just(response);
    }
}
