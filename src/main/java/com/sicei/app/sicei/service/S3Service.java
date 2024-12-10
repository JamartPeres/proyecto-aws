package com.sicei.app.sicei.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.nio.file.Path;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3Service(
            @Value("${aws.access-key-id}") String accessKey,
            @Value("${aws.secret-access-key}") String secretKey,
            @Value("${aws.session-token}") String sessionToken,
            @Value("${aws.region}") String region) {

        AwsCredentials awsCredentials = AwsSessionCredentials.create(accessKey, secretKey, sessionToken);
        this.s3Client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .region(Region.of(region))
                .build();
    }

    public String uploadFile(Path filePath, String key) {
        try {
            // Configurar la solicitud para subir el archivo a S3
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .acl("public-read") // Hacer que el archivo sea accesible públicamente
                    .build();

            // Subir el archivo al bucket de S3
            s3Client.putObject(putObjectRequest, filePath);

            // Retornar la URL pública del archivo
            return String.format("https://%s.s3.amazonaws.com/%s", bucketName, key);

        } catch (S3Exception e) {
            throw new RuntimeException("Error al subir el archivo a S3", e);
        }
    }
}
