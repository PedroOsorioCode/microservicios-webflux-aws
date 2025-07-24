package co.com.microservice.aws.domain.usecase.in;

import co.com.microservice.aws.domain.usecase.in.commons.*;

public interface CountryUseCase extends SaveUseCase, UpdateUseCase, DeleteUseCase,
        ListAllUseCase, FindByShortCodeUseCase, CountByStatusUseCase {
}