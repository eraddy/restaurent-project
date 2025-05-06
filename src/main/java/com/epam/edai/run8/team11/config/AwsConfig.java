package com.epam.edai.run8.team11.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;

@Slf4j
@Configuration
public class AwsConfig {

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.dynamo.accessKey}")
    private String accessKey;

    @Value("${aws.dynamo.secretKey}")
    private String secretKey;

    @Value("${aws.dynamo.sessionToken}")
    private String sessionToken;

    @Value("${aws.dynamo.roleArn}")
    private String roleArn;

    @Bean
    public String sqsUrl(){
        return String.format("https://sqs.%s.amazonaws.com/%s/%s.fifo", awsRegion, "089718700404", "report_sqs_queue_test");
    }

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider(){
        AwsSessionCredentials awsSessionCredentials = AwsSessionCredentials.create(
                accessKey,
                secretKey,
                sessionToken
        );

        StsClient stsClient = StsClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(StaticCredentialsProvider.create(awsSessionCredentials))
                .build();

        AssumeRoleRequest assumeRoleRequest = AssumeRoleRequest.builder()
                .roleArn(roleArn)
                .roleSessionName("assume-role-session-address")
                .build();

        return StsAssumeRoleCredentialsProvider.builder()
                .stsClient(stsClient)
                .refreshRequest(assumeRoleRequest)
                .build();
    }


    @Bean
    public DynamoDbClient provideDynamoDbClient(){
        return DynamoDbClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(awsCredentialsProvider())
                .build();
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(provideDynamoDbClient())
                .build();
    }

    @Bean
    public S3Presigner s3Presigner(){
        return S3Presigner.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(awsCredentialsProvider())
                .build();
    }

    @Bean
    public SqsClient provideSqsClient(){
        return SqsClient
                .builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(awsCredentialsProvider())
                .build();
    }
}
