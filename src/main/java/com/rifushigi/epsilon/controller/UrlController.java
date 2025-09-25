package com.rifushigi.epsilon.controller;

import com.rifushigi.epsilon.dto.UrlShortenRequest;
import com.rifushigi.epsilon.dto.UrlShortenResponse;
import com.rifushigi.epsilon.entity.User;
import com.rifushigi.epsilon.service.UrlShortenerService;
import com.rifushigi.epsilon.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/urls")
@CrossOrigin(origins = "*")
public class UrlController {

    private final UrlShortenerService urlShortenerService;

    private final UserService userService;

    public UrlController(UrlShortenerService urlShortenerService, UserService userService){
        this.urlShortenerService = urlShortenerService;
        this.userService = userService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<?> shortenUrl(@Valid @RequestBody UrlShortenRequest request) {
        try {
            User user = null;

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() &&
                    !(authentication.getPrincipal() instanceof String)) {
                user = userService.getCurrentUser();
            }

            UrlShortenResponse response = urlShortenerService.shortenResponse(request, user);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{shortCode}")
    public void redirectToOriginalUrl(@PathVariable String shortCode,
                                      HttpServletRequest request,
                                      HttpServletResponse response) throws IOException {
        try {
            String originalUrl = urlShortenerService.getOriginalUrl(shortCode);

            // Track click
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            String referer = request.getHeader("Referer");

            urlShortenerService.trackClick(shortCode, ipAddress, userAgent, referer);

            response.sendRedirect(originalUrl);
        } catch (RuntimeException e) {
            response.sendError(HttpStatus.NOT_FOUND.value(), "Short URL not found or expired");
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        } else {
            return xForwardedForHeader.split(",")[0];
        }
    }
}
