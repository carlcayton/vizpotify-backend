package com.arian.vizpotifybackend.controller.dashboard;

import com.arian.vizpotifybackend.dto.*;
import com.arian.vizpotifybackend.dto.analytics.AnalyticsDTO;
import com.arian.vizpotifybackend.dto.analytics.AnalyticsResponseDTO;
import com.arian.vizpotifybackend.services.analytics.AnalyticsService;
import com.arian.vizpotifybackend.services.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class UserInfoController {
    private final ProfileHeaderService profileHeaderService;
    private final UserTopArtistService userTopArtistService;
    private final UserTopTrackService userTopTrackService;
    private final AnalyticsService analyticsService;

    @GetMapping("/{userId}/profileHeader")
    public ResponseEntity<ProfileHeaderDTO> getUserProfileHeader(@PathVariable String userId) {
        ProfileHeaderDTO profileHeaderDTO = profileHeaderService.getProfileHeaderDTO(userId);
        return ResponseEntity.ok(profileHeaderDTO);
    }

    @GetMapping("/{userId}/topArtists")
    public ResponseEntity<Map<String, List<ArtistDTO>>> getUserTopArtists(@PathVariable String userId) {
        Map<String, List<ArtistDTO>> result = userTopArtistService.getUserTopArtists(userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{userId}/topTracks")
    public ResponseEntity<Map<String, List<TrackDTO>>> getUserTopTracks(@PathVariable String userId) {
        Map<String, List<TrackDTO>> result = userTopTrackService.getUserTopItems(userId);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/{userId}/analytics")
    public ResponseEntity<AnalyticsResponseDTO> getUserAnalytics(@PathVariable String userId) {
        AnalyticsDTO analyticsDTO = analyticsService.getAnalyticsForUser(userId);
        boolean isProcessing = analyticsDTO == null;

        if (isProcessing){
            return ResponseEntity.ok(new AnalyticsResponseDTO(null, isProcessing));
        }else {
            return ResponseEntity.ok(new AnalyticsResponseDTO(analyticsDTO, isProcessing));
        }
    }


}