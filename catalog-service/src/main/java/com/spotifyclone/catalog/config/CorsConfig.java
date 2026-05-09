package com.spotifyclone.catalog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**") // Sadece /api/ ile başlayan isteklere bu kuralı uygula
                        .allowedOriginPatterns("*") // Geliştirme ortamında React, Bruno vb. istemcilere izin ver
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // İzin verilen HTTP metotları
                        .allowedHeaders("*") // Tüm başlıklara izin ver
                        .allowCredentials(true); // Kimlik doğrulama (cookie vb.) bilgilerine izin ver
            }
        };
    }
}
