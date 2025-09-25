package com.rifushigi.epsilon.service;

import com.rifushigi.epsilon.dao.ShortUrlRepository;
import com.rifushigi.epsilon.dto.UrlShortenRequest;
import com.rifushigi.epsilon.dto.UrlShortenResponse;
import com.rifushigi.epsilon.entity.ShortUrl;
import com.rifushigi.epsilon.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
public class UrlShortenerService {

    private final ShortUrlRepository shortUrlRepository;
    private final UrlValidationService urlValidationService;
    private final ShortCodeGeneratorService shortCodeGeneratorService;
    private final ClickService clickService;

    @Value("${DEFAULT_TTL}")
    private Long defaultTtl;

    @Value("${BASE_URL}")
    private String baseUrl;

    public UrlShortenerService(
            ShortUrlRepository shortUrlRepository,
            UrlValidationService urlValidationService,
            ShortCodeGeneratorService shortCodeGeneratorService,
            ClickService clickService
    ){
        this.shortUrlRepository = shortUrlRepository;
        this.urlValidationService = urlValidationService;
        this.shortCodeGeneratorService = shortCodeGeneratorService;
        this.clickService = clickService;
    }

    public UrlShortenResponse shortenResponse(UrlShortenRequest request, User user){
        if(!urlValidationService.isUrlValid(request.url())){
            throw new RuntimeException("Invalid URL provided");
        }

        String normalisedUrl = urlValidationService.normalizeUrl(request.url());
        Long ttlSeconds = request.ttlSeconds() != null ? request.ttlSeconds() : defaultTtl;

        String shortCode;
        boolean isCustom = false;

        if(request.customShortCode() != null && !request.customShortCode().trim().isEmpty()) {
            if (!urlValidationService.isValidCustomShortCode(request.customShortCode())){
                throw new RuntimeException("Invalid custom short code format");
            }

            if(shortUrlRepository.existsByShortCode(request.customShortCode())){
                throw new RuntimeException("Custom short code already exists");
            }

            shortCode = request.customShortCode();
            isCustom = true;
        } else {
            do {
                shortCode = shortCodeGeneratorService.generateShortCode();
            } while (shortUrlRepository.existsByShortCode(shortCode));
        }

        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setOriginalUrl(normalisedUrl);
        shortUrl.setShortCode(shortCode);
        shortUrl.setTtlSeconds(ttlSeconds);
        shortUrl.setUser(user);
        shortUrl.setIsCustom(isCustom);

        shortUrl = shortUrlRepository.save(shortUrl);

        return new UrlShortenResponse(
                shortUrl.getOriginalUrl(),
                baseUrl + "/urls/" + shortUrl.getShortCode(),
                shortUrl.getShortCode(),
                shortUrl.getTtlSeconds(),
                shortUrl.getExpiresAt(),
                shortUrl.getIsCustom(),
                shortUrl.getCreatedAt()
        );
    }

    @Cacheable(value = "shortUrls", key = "#shortCode")
    public String getOriginalUrl(String shortCode){
        ShortUrl shortUrl = shortUrlRepository.findByShortCodeAndNotExpired(shortCode, LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("Short url not found or has expired"));

        return shortUrl.getOriginalUrl();
    }

    public void trackClick(String shortCode, String ipAddr, String userAgent, String referer){
        ShortUrl shortUrl = shortUrlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("Short url not found"));

        shortUrlRepository.incrementClickCount(shortUrl.getId());

        clickService.saveClickDetails(shortUrl, ipAddr, userAgent, referer);
    }
}
