package com.arian.vizpotifybackend.controller.dashboard;

import com.arian.vizpotifybackend.dto.artist.ArtistDTO;
import com.arian.vizpotifybackend.dto.artist.RelatedArtistsDTO;
import com.arian.vizpotifybackend.services.artist.ArtistDetailService;
import com.arian.vizpotifybackend.services.artist.RelatedArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/artist")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ArtistDetailController {

    private final RelatedArtistService relatedArtistService;

    @GetMapping("/{artistId}")
    public ResponseEntity<Set<ArtistDTO>> getRelatedArtists(@PathVariable String artistId){
        Set<ArtistDTO> result = relatedArtistService.getRelatedArtists(artistId);
        return ResponseEntity.ok(result);

    }
}
