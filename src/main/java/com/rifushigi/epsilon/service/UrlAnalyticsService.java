package com.rifushigi.epsilon.service;

import com.rifushigi.epsilon.dao.ClickRepository;
import com.rifushigi.epsilon.dao.ShortUrlRepository;
import com.rifushigi.epsilon.dto.UrlAnalyticsResponse;
import com.rifushigi.epsilon.entity.Click;
import com.rifushigi.epsilon.entity.ShortUrl;
import com.rifushigi.epsilon.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class UrlAnalyticsService {

    private final ShortUrlRepository shortUrlRepository;
    private final ClickRepository clickRepository;

    public UrlAnalyticsService(ShortUrlRepository shortUrlRepository, ClickRepository clickRepository){
        this.shortUrlRepository = shortUrlRepository;
        this.clickRepository = clickRepository;
    }

    @Value("${urlshortener.base-url}")
    private String baseUrl;

    public Page<UrlAnalyticsResponse> getUserUrls(User user, Pageable pageable) {
        Page<ShortUrl> shortUrls = shortUrlRepository.findByUserAndNotExpiredOrderByCreatedAtDesc(user, LocalDateTime.now(), pageable);

        return shortUrls.map(this::convertToAnalyticsResponse);
    }

    public Page<UrlAnalyticsResponse> getUserUrlsByClickCount(User user, Pageable pageable) {
        Page<ShortUrl> shortUrls = shortUrlRepository.findByUserOrderByClickCountDesc(user, pageable);

        return shortUrls.map(this::convertToAnalyticsResponse);
    }

    public UrlAnalyticsResponse getUrlAnalytics(User user, String shortCode) {
        ShortUrl shortUrl = shortUrlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("Short URL not found"));

        // Check if user owns this URL
        if (!shortUrl.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied: You don't own this URL");
        }

        return convertToAnalyticsResponse(shortUrl);
    }

    private UrlAnalyticsResponse convertToAnalyticsResponse(ShortUrl shortUrl) {
        // Get last click time
        LocalDateTime lastClickedAt = null;
        Page<Click> lastClickPage = clickRepository.findLatestClicksByShortUrl(shortUrl, Pageable.ofSize(1));
        List<Click> lastClick = lastClickPage.getContent();
        if (!lastClick.isEmpty()) {
            lastClickedAt = lastClick.getFirst().getClickedAt();
        }

        return new UrlAnalyticsResponse(
                shortUrl.getId(),
                shortUrl.getOriginalUrl(),
                shortUrl.getShortCode(),
                baseUrl + "/urls/" + shortUrl.getShortCode(),
                shortUrl.getClickCount(),
                shortUrl.getTtlSeconds(),
                shortUrl.getExpiresAt(),
                shortUrl.getIsCustom(),
                shortUrl.getCreatedAt(),
                lastClickedAt
        );
    }
}
