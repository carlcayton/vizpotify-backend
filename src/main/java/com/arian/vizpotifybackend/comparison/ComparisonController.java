package com.arian.vizpotifybackend.comparison;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comparison")
@RequiredArgsConstructor
public class ComparisonController {

    private final ComparisonService comparisonService;
    private final UserComparisonService userComparisonService;

    @GetMapping("/{userId1}/{userId2}")
    public ResponseEntity<ComparisonDto> compareUsers(
            @PathVariable String userId1,
            @PathVariable String userId2) {
        ComparisonDto comparisonDto = comparisonService.compareUsers(userId1, userId2);
        return ResponseEntity.ok(comparisonDto);
    }

    @GetMapping("/users")
    public ResponseEntity<UserComparisonDto> compareUserAnalytics(
            @RequestParam String userId1,
            @RequestParam String userId2) {
        UserComparisonDto userComparisonDto = userComparisonService.compareUsers(userId1, userId2);
        return ResponseEntity.ok(userComparisonDto);
    }
}
