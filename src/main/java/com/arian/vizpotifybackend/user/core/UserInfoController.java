package com.arian.vizpotifybackend.user.core;

import com.arian.vizpotifybackend.artist.ArtistDto;
import com.arian.vizpotifybackend.track.TrackDto;
import com.arian.vizpotifybackend.user.profile.ProfileHeaderDto;
import com.arian.vizpotifybackend.user.profile.ProfileHeaderService;
import com.arian.vizpotifybackend.user.topitems.artist.UserTopArtistService;
import com.arian.vizpotifybackend.user.topitems.track.UserTopTrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserInfoController {
    private final ProfileHeaderService profileHeaderService;
    private final UserTopArtistService userTopArtistService;
    private final UserTopTrackService userTopTrackService;

    @GetMapping("/{userId}/profileHeader")
    public ResponseEntity<ProfileHeaderDto> getUserProfileHeader(@PathVariable String userId) {
        ProfileHeaderDto profileHeaderDto = profileHeaderService.getProfileHeaderDto(userId);
        return ResponseEntity.ok(profileHeaderDto);
    }

    @GetMapping("/{userId}/topArtists")
    public ResponseEntity<Map<String, List<ArtistDto>>> getUserTopArtists(@PathVariable String userId) {
        Map<String, List<ArtistDto>> result = userTopArtistService.getUserTopArtists(userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{userId}/topTracks")
    public ResponseEntity<Map<String, List<TrackDto>>> getUserTopTracks(@PathVariable String userId) {
        Map<String, List<TrackDto>> result = userTopTrackService.getUserTopItems(userId);
        return ResponseEntity.ok(result);
    }

}