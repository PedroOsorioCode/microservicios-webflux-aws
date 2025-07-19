package co.com.microservice.aws.domain.usecase.in;

public interface CountryUseCase extends SaveUseCase, UpdateUseCase, DeleteUseCase,
        ListAllUseCase, FindByShortCodeUseCase, CountByStatusUseCase{
}