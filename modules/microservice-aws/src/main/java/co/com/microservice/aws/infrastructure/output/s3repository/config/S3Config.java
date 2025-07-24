package co.com.microservice.aws.infrastructure.output.s3repository.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3AsyncClientBuilder;

import java.net.URI;

@Configuration
public class S3Config {
    @Profile({ "!local" })
    @Bean(name = "s3Connection")
    S3AsyncClient s3AsyncClient(S3ConnectionProperties s3Properties) {
        return getBuilder(s3Properties).build();
    }

    @Profile("local")
    @Bean(name = "s3Connection")
    S3AsyncClient localS3AsyncClient(S3ConnectionProperties props) {
        return S3AsyncClient.builder()
                .region(Region.of(props.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey())))
                .endpointOverride(URI.create(props.getEndpoint()))
                .forcePathStyle(true)
                .build();
    }

    private S3AsyncClientBuilder getBuilder(S3ConnectionProperties s3Properties) {
        return S3AsyncClient.builder().region(Region.of(s3Properties.getRegion()));
    }
}