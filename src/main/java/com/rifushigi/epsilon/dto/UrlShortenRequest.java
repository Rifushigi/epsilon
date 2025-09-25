package com.rifushigi.epsilon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UrlShortenRequest(
        @NotBlank(message = "URL is required")
        String url,

        @Positive(message = "TTL must be positive")
        Long ttlSeconds,

        @Size(min = 3, max = 20, message = "Custom short code must be between 3 and 20 characters")
        String customShortCode
) {
}
