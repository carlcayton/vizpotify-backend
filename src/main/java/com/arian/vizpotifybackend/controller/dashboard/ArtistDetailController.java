package com.arian.vizpotifybackend.controller.dashboard;

import com.arian.vizpotifybackend.dto.artist.RelatedArtistsDTO;
import com.arian.vizpotifybackend.services.artist.ArtistDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/artist")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ArtistDetailController {

    private final ArtistDetailService artistDetailService;

    @GetMapping("/{artistId}")
    public ResponseEntity<RelatedArtistsDTO> getRelatedArtists(@PathVariable String artistId){

        return null;

    }
}
