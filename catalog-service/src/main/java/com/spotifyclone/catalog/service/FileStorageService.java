package com.spotifyclone.catalog.service;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.SetBucketPolicyArgs;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.public-url}")
    private String minioPublicUrl;

    private boolean bucketPrepared;

    public FileStorageService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @PostConstruct
    void warmUpBucket() {
        prepareBucket(false);
    }

    public String uploadFile(MultipartFile file, String folder) {
        validateFile(file);
        prepareBucket(true);

        try {
            String fileName = buildObjectName(file, folder);

            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(fileName)
                                .stream(inputStream, file.getSize(), -1)
                                .contentType(resolveContentType(file))
                                .build()
                );
            }

            return fileName;

        } catch (Exception e) {
            throw new RuntimeException("Dosya MinIO'ya yüklenirken hata oluştu: " + e.getMessage(), e);
        }
    }

    public String buildPublicUrl(String storedValue) {
        if (!StringUtils.hasText(storedValue)) {
            return null;
        }

        prepareBucket(false);

        if (isAbsoluteUrl(storedValue)) {
            String extractedObjectName = extractObjectNameFromUrl(storedValue);
            return extractedObjectName != null
                    ? buildUrlFromObjectName(extractedObjectName)
                    : storedValue;
        }

        return buildUrlFromObjectName(storedValue);
    }

    private String buildObjectName(MultipartFile file, String folder) {
        String originalFileName = Optional.ofNullable(file.getOriginalFilename())
                .filter(StringUtils::hasText)
                .orElse("file");

        return folder
                + "/"
                + UUID.randomUUID()
                + "-"
                + originalFileName.replace(" ", "_");
    }

    private String buildUrlFromObjectName(String objectName) {
        return trimTrailingSlash(minioPublicUrl)
                + "/"
                + bucketName
                + "/"
                + stripLeadingSlash(objectName);
    }

    private String extractObjectNameFromUrl(String storedValue) {
        try {
            String path = stripLeadingSlash(URI.create(storedValue).getPath());
            String bucketPrefix = bucketName + "/";

            if (path.startsWith(bucketPrefix)) {
                return path.substring(bucketPrefix.length());
            }

            return null;
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private boolean isAbsoluteUrl(String value) {
        return value.startsWith("http://") || value.startsWith("https://");
    }

    private String stripLeadingSlash(String value) {
        return value.startsWith("/") ? value.substring(1) : value;
    }

    private String trimTrailingSlash(String value) {
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    private synchronized void prepareBucket(boolean failOnError) {
        if (bucketPrepared) {
            return;
        }

        try {
            ensureBucketExists();
            ensureBucketIsPublic();
            bucketPrepared = true;
        } catch (Exception exception) {
            if (failOnError) {
                if (exception instanceof RuntimeException) {
                    throw (RuntimeException) exception;
                }

                throw new IllegalStateException("MinIO bucket hazırlanamadı: " + bucketName, exception);
            }

            log.warn("MinIO bucket hazır değil. Public erişim tekrar denenecek. bucket={}", bucketName, exception);
        }
    }

    private void ensureBucketExists() {
        try {
            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );

            if (!bucketExists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
            }

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "MinIO bucket hazırlanamadı: " + bucketName,
                    exception
            );
        }
    }

    private void ensureBucketIsPublic() {
        try {
            String publicReadPolicy = String.format(
                    "{"
                            + "\"Version\":\"2012-10-17\","
                            + "\"Statement\":["
                            + "{"
                            + "\"Effect\":\"Allow\","
                            + "\"Principal\":{\"AWS\":[\"*\"]},"
                            + "\"Action\":[\"s3:GetObject\"],"
                            + "\"Resource\":[\"arn:aws:s3:::%s/*\"]"
                            + "}"
                            + "]"
                            + "}",
                    bucketName
            );

            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                            .bucket(bucketName)
                            .config(publicReadPolicy)
                            .build()
            );
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "MinIO bucket public erişime açılamadı: " + bucketName,
                    exception
            );
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Dosya boş olamaz.");
        }
    }

    private String resolveContentType(MultipartFile file) {
        return file.getContentType() != null
                ? file.getContentType()
                : "application/octet-stream";
    }
}
