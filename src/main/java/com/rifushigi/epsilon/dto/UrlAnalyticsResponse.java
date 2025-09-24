package com.rifushigi.epsilon.dto;

import java.time.LocalDateTime;

public record UrlAnalyticsResponse(
        Long id,
        String originalUrl,
        String shortCode,
        String shortUrl,
        String clickCount,
        String ttlSeconds,
        LocalDateTime expiresAt,
        boolean isCustom,
        LocalDateTime createdAt,
        LocalDateTime lastClickedAt
) {
}
