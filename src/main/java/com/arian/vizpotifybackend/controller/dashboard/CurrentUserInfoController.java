package com.arian.vizpotifybackend.controller.dashboard;

import com.arian.vizpotifybackend.dto.ProfileHeaderDTO;
import com.arian.vizpotifybackend.dto.ArtistDTO;
import com.arian.vizpotifybackend.dto.TrackDTO;
import com.arian.vizpotifybackend.model.UserDetail;
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
@RequestMapping("/api/v1/me")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class CurrentUserInfoController {

    private final ProfileHeaderService profileHeaderService;
    private final UserTopArtistService userTopArtistService;
    private final UserTopTrackServiceImpl userTopTrackService;

    @GetMapping("/profileHeader")
      public ResponseEntity<ProfileHeaderDTO> getProfileHeader(
            HttpServletResponse response,
            Authentication auth) throws IOException {
        UserDetail userDetail = (UserDetail) auth.getPrincipal();
       ProfileHeaderDTO profileHeaderDTO = profileHeaderService
               .getProfileHeaderDTO(userDetail.getSpotifyId());
        return ResponseEntity.ok(profileHeaderDTO);
    }

    @GetMapping("/userTopArtists")
    public ResponseEntity<Map<String, List<ArtistDTO>>> getTopArtists(
            HttpServletResponse response,
            Authentication auth) throws IOException {
        UserDetail userDetail = (UserDetail) auth.getPrincipal();
        Map<String, List<ArtistDTO>> result = userTopArtistService.getUserTopArtists(userDetail.getSpotifyId());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/userTopTracks")
    public ResponseEntity<Map<String,List<TrackDTO>>> getTopSongs(
            HttpServletResponse response,
            Authentication auth) throws IOException {

        UserDetail userDetail = (UserDetail) auth.getPrincipal();
        Map<String, List<TrackDTO>> result = userTopTrackService.getUserTopItems(userDetail.getSpotifyId());
        return ResponseEntity.ok(result);
    }

}
