package com.arian.vizpotifybackend.controller.comparison;

import com.arian.vizpotifybackend.dto.analytics.ComparisonDTO;
import com.arian.vizpotifybackend.model.UserDetail;
import com.arian.vizpotifybackend.services.analytics.ComparisonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comparison")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:3000")
public class ComparisonController {

    private final ComparisonService comparisonService;

    @PostMapping("/{userId}/compare")
    public ResponseEntity<ComparisonDTO> compare(@PathVariable String userId,
                                                 Authentication authentication) {
        UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        ComparisonDTO comparisonDTO = comparisonService.getComparison(userDetail.getSpotifyId(), userId);

        return ResponseEntity.ok(new ComparisonDTO());
    }
}
