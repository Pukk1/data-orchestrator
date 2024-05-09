package com.ivan.model.orchestrator.configuration.minio;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioClientConfiguration {

    private final String endpoint;
    private final String accessKey;
    private final String secretKey;
    private final String bucketName;

    private MinioClient minioClient;

    public MinioClientConfiguration(
            @Value("${minio.url}") String endpoint,
            @Value("${minio.access-key}") String accessKey,
            @Value("${minio.secret-key}") String secretKey,
            @Value("${minio.bucket}") String bucketName
    ) {
        this.endpoint = endpoint;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.bucketName = bucketName;
    }

    @Bean
    public MinioClient minioClient() {
        minioClient = createMinioClient();

        try {
            if (isBucketNotExists()) {
                minioClient.makeBucket(
                        MakeBucketArgs
                                .builder()
                                .bucket(bucketName)
                                .build()
                );
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return minioClient;
    }

    private MinioClient createMinioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    private boolean isBucketNotExists() {
        var bucketExistsArgs = BucketExistsArgs.builder().bucket(bucketName).build();
        try {
            return !minioClient.bucketExists(bucketExistsArgs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
