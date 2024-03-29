package com.arian.vizpotifybackend.controller.dashboard;


import com.arian.vizpotifybackend.model.AudioFeature;
import com.arian.vizpotifybackend.services.track.AudioFeatureService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/track")
@RequiredArgsConstructor
public class TrackDetailController {

    private final AudioFeatureService audioFeatureService;

    @GetMapping("/audiofeature/{trackId}")
    public ResponseEntity<AudioFeature> getTrackAudioFeature(@PathVariable String trackId){
        return audioFeatureService.getAudioFeature(trackId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
