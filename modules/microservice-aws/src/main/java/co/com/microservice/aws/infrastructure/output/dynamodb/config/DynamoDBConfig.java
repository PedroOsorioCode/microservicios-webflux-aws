package co.com.microservice.aws.infrastructure.output.dynamodb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

import java.net.URI;

@Configuration
public class DynamoDBConfig {
    @Bean
    @Profile({"local"})
    public DynamoDbAsyncClient amazonDynamoDB(@Value("${adapters.dynamodb.endpoint}") String endpoint) {
        return DynamoDbAsyncClient.builder()
                .credentialsProvider(ProfileCredentialsProvider.create("default"))
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(SdkSystemSetting.AWS_REGION.environmentVariable()))
                .build();
    }
    @Bean
    @Profile({"!local"})
    public DynamoDbAsyncClient amazonDynamoDBAsync(@Value("${adapters.dynamodb.region}") String region) {
        return DynamoDbAsyncClient.builder()
                .credentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
                .region(Region.of(region)).build();
    }
    @Bean
    public DynamoDbEnhancedAsyncClient dynamoClient(DynamoDbAsyncClient dynamoDbAsyncClient){
        return DynamoDbEnhancedAsyncClient.builder()
                .dynamoDbClient(dynamoDbAsyncClient)
                .build();
    }
}