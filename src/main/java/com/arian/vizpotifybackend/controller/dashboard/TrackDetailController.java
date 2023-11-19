package com.arian.vizpotifybackend.controller.dashboard;


import com.arian.vizpotifybackend.model.AudioFeature;
import com.arian.vizpotifybackend.services.track.AudioFeatureService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/track")
@CrossOrigin(origins = "http://localhost:3000") // Adjust the origins as per your frontend deployment
@RequiredArgsConstructor
public class TrackDetailController {

    private final AudioFeatureService audioFeatureService;

    @GetMapping("/audiofeature/{trackId}")
    public ResponseEntity<AudioFeature> getTrackAudioFeature(@PathVariable String trackId){
        System.out.println("test");
        return audioFeatureService.getAudioFeature(trackId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
