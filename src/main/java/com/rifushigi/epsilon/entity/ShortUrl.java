package com.rifushigi.epsilon.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "short_urls", indexes = {
        @Index(name = "idx_short_code", columnList = "short_code", unique = true),
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_expires_at", columnList = "expires_at")
})
public class ShortUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @NotBlank(message = "Original URL is required")
    @Column(name = "original_url", nullable = false)
    private String originalUrl;

    @NotBlank(message = "Short code is required")
    @Column(name = "short_code", unique = true, nullable = false)
    private String shortCode;

    @Column(name = "column_count", nullable = false)
    private Long clickCount = 0L;

    @NotNull(message = "TTL is required")
    @Positive(message = "TTL must be positive")
    @Column(name = "ttl_seconds", nullable = false)
    private Long ttlSeconds;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "is_custom", nullable = false)
    private Boolean isCustom = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
