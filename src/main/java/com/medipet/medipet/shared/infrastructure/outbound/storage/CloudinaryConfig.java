package com.medipet.medipet.shared.infrastructure.outbound.storage;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
class CloudinaryConfig {

    @Bean
    Cloudinary cloudinary(@Value("${CLOUDINARY_CLOUD_NAME:}") String cloudName,
                          @Value("${CLOUDINARY_API_KEY:}") String apiKey,
                          @Value("${CLOUDINARY_API_SECRET:}") String apiSecret) {
        return new Cloudinary(Map.of(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));
    }
}
