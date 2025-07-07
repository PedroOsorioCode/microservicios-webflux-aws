package co.com.microservicio.aws.secretsmanager.helper;

import java.util.function.Function;

import co.com.bancolombia.secretsmanager.api.GenericManager;
import co.com.bancolombia.secretsmanager.api.exceptions.SecretException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecretsHelper<T, R> {

    protected String secretName;
    protected Class<T> clazz;

    protected SecretsHelper(Class<T> clazz, String secretName) {
        this.clazz = clazz;
        this.secretName = secretName;
    }

    protected R createConfigFromSecret(GenericManager manager, Function<T, R> configMaker) throws SecretException {
        try {
            return configMaker.apply(manager.getSecret(secretName, clazz));
        } catch (SecretException exception) {
            throw new SecretException(exception.getMessage());
        }
    }
}
