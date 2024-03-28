package com.arian.vizpotifybackend.controller.comparison;

import com.arian.vizpotifybackend.dto.comparison.ComparisonDTO;
import com.arian.vizpotifybackend.model.UserDetail;
import com.arian.vizpotifybackend.services.analytics.ComparisonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comparison")
@RequiredArgsConstructor
public class ComparisonController {

    private final ComparisonService comparisonService;

    @PostMapping("/{userId}")
    public ResponseEntity<ComparisonDTO> compare(@PathVariable String userId,
                                                 Authentication authentication) {
        UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        ComparisonDTO comparisonDTO = comparisonService.getComparison(userDetail.getSpotifyId(), userId);

        return ResponseEntity.ok(comparisonDTO);
    }
}
