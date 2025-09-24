package com.rifushigi.epsilon.dto;

import java.time.LocalDateTime;

public record UrlShortenResponse(
        String originalUrl,
        String shortUrl,
        String shortCode,
        Long ttlSeconds,
        LocalDateTime expiresAt,
        boolean isCustom,
        LocalDateTime createdAt
) {
}
