
package com.arian.vizpotifybackend.artist;

import com.arian.vizpotifybackend.track.TrackDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/artist")
@RequiredArgsConstructor
public class ArtistDetailController {

    private final RelatedArtistService relatedArtistService;
    private final ArtistTopTracksService artistTopTracksService;

    @GetMapping("/{artistId}")
    public ResponseEntity<Map<String, Object>> getArtistExtraInfo(@PathVariable String artistId){
        Map<String,Object> result = new HashMap<>();
        List<ArtistDto> artistDtoS= relatedArtistService.getRelatedArtists(artistId);
        List<TrackDto> trackDtoS =  artistTopTracksService.getArtistTopTracks(artistId);
        result.put("artistDtoS",artistDtoS);
        result.put("trackDtoS",trackDtoS);
        return ResponseEntity.ok(result);
    }
}
