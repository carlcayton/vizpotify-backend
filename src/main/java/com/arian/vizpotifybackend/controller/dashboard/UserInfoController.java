package com.arian.vizpotifybackend.controller.dashboard;

import com.arian.vizpotifybackend.dto.*;
import com.arian.vizpotifybackend.model.UserDetail;
import com.arian.vizpotifybackend.services.user.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
public class UserInfoController {
    private final ProfileHeaderService profileHeaderService;
    private final UserTopArtistService userTopArtistService;
    private final UserTopTrackServiceImpl userTopTrackService;
    private final AnalyticsService analyticsService;
    private final CommentService commentService;

    // 

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
    public ResponseEntity<AnalyticsDTO> getUserAnalytics(@PathVariable String userId) {
        AnalyticsDTO analytics = analyticsService.getAnalyticsForUser(userId);
        return ResponseEntity.ok(analytics);
    }

    @PostMapping
    public ResponseEntity<CommentDTO> postComment(@PathVariable Long userId,
                                                  @RequestBody CommentDTO commentDTO,
                                                  Authentication authentication) {
        // Ensure the logged-in user is the one posting the comment
        UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        commentDTO.setAuthorSpotifyId(userDetail.getSpotifyId()); // Set author's Spotify ID
        CommentDTO createdComment = commentService.createComment(userId, commentDTO);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CommentDTO>> getCommentsForUser(@PathVariable Long userId) {
        List<CommentDTO> comments = commentService.getCommentsByUserId(userId);
        return ResponseEntity.ok(comments);
    }
}