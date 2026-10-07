package com.spotifyclone.catalog.service;

import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class FileStorageServiceTest {

    @Mock
    private MinioClient minioClient;

    private FileStorageService fileStorageService;

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageService(minioClient);
        ReflectionTestUtils.setField(fileStorageService, "bucketName", "spotify-files");
        ReflectionTestUtils.setField(fileStorageService, "minioPublicUrl", "http://192.168.1.190:9000/");
        ReflectionTestUtils.setField(fileStorageService, "bucketPrepared", true);
    }

    @Test
    void buildPublicUrl_WhenStoredValueIsObjectName_ShouldUseConfiguredPublicUrl() {
        String result = fileStorageService.buildPublicUrl("images/artist-cover.jpg");

        assertEquals(
                "http://192.168.1.190:9000/spotify-files/images/artist-cover.jpg",
                result
        );
    }

    @Test
    void buildPublicUrl_WhenStoredValueIsLegacyAbsoluteUrl_ShouldNormalizeHost() {
        String result = fileStorageService.buildPublicUrl(
                "http://localhost:9005/spotify-files/images/artist-cover.jpg"
        );

        assertEquals(
                "http://192.168.1.190:9000/spotify-files/images/artist-cover.jpg",
                result
        );
    }
}
