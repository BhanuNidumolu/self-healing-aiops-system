package com.selfheal.supervisorservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;
import java.time.Duration;

@Configuration
@Slf4j
public class DynamoDbConfig {

    @Value("${AWS_DYNAMODB_ENDPOINT:http://sentinel-dynamodb:8000}")
    private String dynamodbEndpoint;

    @Bean
    public DynamoDbClient dynamoDbClient() {
        log.info("==================================================");
        log.info("INITIALIZING DYNAMODB CLIENT -> {}", dynamodbEndpoint);
        log.info("==================================================");

        return DynamoDbClient.builder()
                .endpointOverride(URI.create(dynamodbEndpoint))
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("dummy", "dummy")
                ))
                .httpClientBuilder(UrlConnectionHttpClient.builder()
                        .connectionTimeout(Duration.ofSeconds(3))
                        .socketTimeout(Duration.ofSeconds(3)))
                .build();
    }
}