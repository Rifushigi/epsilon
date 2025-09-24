package com.rifushigi.epsilon.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UrlAnalyticsResponse(
        UUID id,
        String originalUrl,
        String shortCode,
        String shortUrl,
        Long clickCount,
        Long ttlSeconds,
        LocalDateTime expiresAt,
        boolean isCustom,
        LocalDateTime createdAt,
        LocalDateTime lastClickedAt
) {
}
