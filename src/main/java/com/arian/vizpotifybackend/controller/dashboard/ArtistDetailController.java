package com.arian.vizpotifybackend.controller.dashboard;

import com.arian.vizpotifybackend.dto.ArtistDTO;
import com.arian.vizpotifybackend.dto.TrackDTO;
import com.arian.vizpotifybackend.services.artist.ArtistTopTracksService;
import com.arian.vizpotifybackend.services.artist.RelatedArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/artist")
@RequiredArgsConstructor
public class ArtistDetailController {

    private final RelatedArtistService relatedArtistService;
    private final ArtistTopTracksService artistTopTracksService;

    @GetMapping("/{artistId}")
    public ResponseEntity<Map<String, Object>> getArtistExtraInfo(@PathVariable String artistId){
        Map<String,Object> result = new HashMap<>();
        List<ArtistDTO> artistDTOS= relatedArtistService.getRelatedArtists(artistId);
        List<TrackDTO> trackDTOS =  artistTopTracksService.getArtistTopTracks(artistId);
        result.put("artistDTOS",artistDTOS);
        result.put("trackDTOS",trackDTOS);
        return ResponseEntity.ok(result);
    }
}
