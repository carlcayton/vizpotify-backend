package com.arian.vizpotifybackend.track;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/track")
@RequiredArgsConstructor
public class TrackDetailController {

    private final AudioFeatureService audioFeatureService;

    @GetMapping("/audiofeature/{trackId}")
    public ResponseEntity<AudioFeature> getAudioFeature(@PathVariable String trackId){
        return audioFeatureService.getAudioFeature(trackId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
