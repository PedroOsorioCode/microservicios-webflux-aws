package co.com.microservicio.aws.dynamodb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

import java.net.URI;

@Configuration
public class DynamoDBConfig {

    @Bean
    @Profile({ "local" })
    DynamoDbAsyncClient amazonDynamoDB(@Value("${aws.dynamodb.endpoint}") String endpoint,
                                       @Value("${aws.region}") String region) {
        return DynamoDbAsyncClient.builder().credentialsProvider(ProfileCredentialsProvider.create("default"))
                .endpointOverride(URI.create(endpoint)).region(Region.of(region)).build();
    }

    @Bean
    @Profile({ "!local" })
    DynamoDbAsyncClient amazonDynamoDBAsync(@Value("${aws.region}") String region) {
        return DynamoDbAsyncClient.builder().credentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
                .region(Region.of(region)).build();
    }

    @Bean
    DynamoDbEnhancedAsyncClient getDynamoDbEnhancedAsyncClient(DynamoDbAsyncClient dynamoDbAsyncClient) {
        return DynamoDbEnhancedAsyncClient.builder().dynamoDbClient(dynamoDbAsyncClient).build();
    }

}
