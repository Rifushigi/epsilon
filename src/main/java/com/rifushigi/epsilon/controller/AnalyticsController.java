package com.rifushigi.epsilon.controller;

import com.rifushigi.epsilon.dto.UrlAnalyticsResponse;
import com.rifushigi.epsilon.entity.User;
import com.rifushigi.epsilon.service.UrlAnalyticsService;
import com.rifushigi.epsilon.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final UrlAnalyticsService analyticsService;
    private final UserService userService;

    public AnalyticsController(UrlAnalyticsService analyticsService, UserService userService){
        this.analyticsService = analyticsService;
        this.userService = userService;
    }


    @GetMapping("/my-urls")
    public ResponseEntity<Page<UrlAnalyticsResponse>> getMyUrls(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        User user = userService.getCurrentUser();
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<UrlAnalyticsResponse> urls = analyticsService.getUserUrls(user, pageable);
        return ResponseEntity.ok(urls);
    }

    @GetMapping("/my-urls/top")
    public ResponseEntity<Page<UrlAnalyticsResponse>> getTopUrls(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        User user = userService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);

        Page<UrlAnalyticsResponse> urls = analyticsService.getUserUrlsByClickCount(user, pageable);
        return ResponseEntity.ok(urls);
    }

    @GetMapping("/url/{shortCode}")
    public ResponseEntity<UrlAnalyticsResponse> getUrlAnalytics(@PathVariable String shortCode) {
        try {
            User user = userService.getCurrentUser();
            UrlAnalyticsResponse analytics = analyticsService.getUrlAnalytics(user, shortCode);
            return ResponseEntity.ok(analytics);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
