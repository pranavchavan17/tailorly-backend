package com.tailorly.tailorly_backend.config;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class CloudinaryConfig {

    private final CloudinaryProperties cloudinaryProperties;

    @Bean
    public Cloudinary cloudinary() {

        return new Cloudinary(
                Map.of(
                        "cloud_name", cloudinaryProperties.getCloudName(),
                        "api_key", cloudinaryProperties.getApiKey(),
                        "api_secret", cloudinaryProperties.getApiSecret()
                )
        );
    }
}