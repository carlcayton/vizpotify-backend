package com.arian.vizpotifybackend.controller.dashboard;

import com.arian.vizpotifybackend.dto.ProfileHeaderDTO;
import com.arian.vizpotifybackend.dto.artist.ArtistDTO;
import com.arian.vizpotifybackend.dto.artist.UserTopArtistsDTO;
import com.arian.vizpotifybackend.model.UserDetail;
import com.arian.vizpotifybackend.services.user.ProfileHeaderService;
import com.arian.vizpotifybackend.services.user.UserTopArtistService;
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

    @GetMapping("/profileHeader")
      public ResponseEntity<ProfileHeaderDTO> getProfileHeader(
            HttpServletResponse response,
            Authentication auth) throws IOException {
        UserDetail userDetail = (UserDetail) auth.getPrincipal();
       ProfileHeaderDTO profileHeaderDTO = profileHeaderService
               .getProfileHeaderDTO(userDetail.getSpotifyId());
        System.out.println("profileHeader");
        return ResponseEntity.ok(profileHeaderDTO);
    }

    @GetMapping("/userTopArtist")
    public ResponseEntity<Map<String, List<ArtistDTO>>> getTopArtists(
            HttpServletResponse response,
            Authentication auth) throws IOException {
        UserDetail userDetail = (UserDetail) auth.getPrincipal();
        Map<String, List<ArtistDTO>> result = userTopArtistService.getUserTopArtists(userDetail.getSpotifyId());
        System.out.println("userTopArtist");
        return ResponseEntity.ok(result);
    }
}
