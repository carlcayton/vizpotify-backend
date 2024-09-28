package com.arian.vizpotifybackend.analytics.core;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    @GetMapping("/{userId}/analytics")
    public ResponseEntity<AnalyticsDto> getUserAnalytics(@PathVariable String userId) {
        AnalyticsDto analyticsDto = analyticsService.getAnalyticsForUser(userId);
        return ResponseEntity.ok(analyticsDto);
    }
}
