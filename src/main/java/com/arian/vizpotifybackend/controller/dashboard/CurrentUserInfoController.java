package com.arian.vizpotifybackend.controller.dashboard;

import com.arian.vizpotifybackend.dto.AnalyticsDTO;
import com.arian.vizpotifybackend.dto.ProfileHeaderDTO;
import com.arian.vizpotifybackend.dto.ArtistDTO;
import com.arian.vizpotifybackend.dto.TrackDTO;
import com.arian.vizpotifybackend.model.UserDetail;
import com.arian.vizpotifybackend.services.user.AnalyticsService;
import com.arian.vizpotifybackend.services.user.ProfileHeaderService;
import com.arian.vizpotifybackend.services.user.UserTopArtistService;
import com.arian.vizpotifybackend.services.user.UserTopTrackServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class CurrentUserInfoController {

    private final ProfileHeaderService profileHeaderService;
    private final UserTopArtistService userTopArtistService;
    private final UserTopTrackServiceImpl userTopTrackService;
    private final AnalyticsService analyticsService;

    @GetMapping("/me/profileHeader")
      public ResponseEntity<ProfileHeaderDTO> getProfileHeader(
            Authentication auth) throws IOException {
        UserDetail userDetail = (UserDetail) auth.getPrincipal();
       ProfileHeaderDTO profileHeaderDTO = profileHeaderService
               .getProfileHeaderDTO(userDetail.getSpotifyId());
        return ResponseEntity.ok(profileHeaderDTO);
    }

    @GetMapping("/me/userTopArtists")
    public ResponseEntity<Map<String, List<ArtistDTO>>> getTopArtists(
            Authentication auth) throws IOException {
        UserDetail userDetail = (UserDetail) auth.getPrincipal();
        Map<String, List<ArtistDTO>> result = userTopArtistService.getUserTopArtists(userDetail.getSpotifyId());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/me/userTopTracks")
    public ResponseEntity<Map<String,List<TrackDTO>>> getTopSongs(
            HttpServletResponse response,
            Authentication auth) throws IOException {

        UserDetail userDetail = (UserDetail) auth.getPrincipal();
        Map<String, List<TrackDTO>> result = userTopTrackService.getUserTopItems(userDetail.getSpotifyId());
        return ResponseEntity.ok(result);
    }
    @GetMapping("/me/analytics")
    public ResponseEntity<AnalyticsDTO> getUserAnalytics(
            HttpServletResponse response,
            Authentication auth) throws IOException {
        UserDetail userDetail = (UserDetail) auth.getPrincipal();
            AnalyticsDTO analytics = analyticsService.getAnalyticsForUser(userDetail.getSpotifyId());
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/{userId}/profileHeader")
    public ResponseEntity<ProfileHeaderDTO> getOtherUserProfileHeader(@PathVariable String userId) {
        ProfileHeaderDTO profileHeaderDTO = profileHeaderService.getProfileHeaderDTO(userId);
        return ResponseEntity.ok(profileHeaderDTO);
    }

    @GetMapping("/{userId}/userTopArtists")
    public ResponseEntity<Map<String, List<ArtistDTO>>> getOtherUserTopArtists(@PathVariable String userId) {
        Map<String, List<ArtistDTO>> result = userTopArtistService.getUserTopArtists(userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{userId}/userTopTracks")
    public ResponseEntity<Map<String, List<TrackDTO>>> getOtherUserTopTracks(@PathVariable String userId) {
        Map<String, List<TrackDTO>> result = userTopTrackService.getUserTopItems(userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{userId}/analytics")
    public ResponseEntity<AnalyticsDTO> getOtherUserAnalytics(@PathVariable String userId) {
        AnalyticsDTO analytics = analyticsService.getAnalyticsForUser(userId);
        return ResponseEntity.ok(analytics);
    }

}
