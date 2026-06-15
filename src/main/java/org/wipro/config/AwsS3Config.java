package org.wipro.config;

import jakarta.annotation.PostConstruct;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

import software.amazon.awssdk.regions.Region;

import software.amazon.awssdk.services.s3.S3Client;

import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;

@ApplicationScoped
public class AwsS3Config {

    @ConfigProperty(name = "aws.access-key")
    String accessKey;

    @ConfigProperty(name = "aws.secret-key")
    String secretKey;

    @ConfigProperty(name = "aws.region")
    String region;

    @ConfigProperty(name = "aws.s3.bucket-name")
    String bucketName;

    private S3Client s3Client;

    @PostConstruct
    void init() {

        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                accessKey,
                secretKey);

        s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(credentials))
                .build();

        createBucketIfNotExists();
    }

    private void createBucketIfNotExists() {

        try {

            s3Client.headBucket(
                    HeadBucketRequest.builder()
                            .bucket(bucketName)
                            .build());

        } catch (Exception ex) {

            s3Client.createBucket(
                    CreateBucketRequest.builder()
                            .bucket(bucketName)
                            .build());
        }
    }

    @Produces
    @ApplicationScoped
    public S3Client s3Client() {
        return s3Client;
    }
}