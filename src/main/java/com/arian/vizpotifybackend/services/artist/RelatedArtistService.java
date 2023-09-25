package com.arian.vizpotifybackend.services.artist;

import com.arian.vizpotifybackend.model.ArtistDetail;
import com.arian.vizpotifybackend.model.RelatedArtist;
import com.arian.vizpotifybackend.model.composite.RelatedArtistId;
import com.arian.vizpotifybackend.repository.RelatedArtistRepository;
import com.arian.vizpotifybackend.services.spotify.SpotifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RelatedArtistService {

    private final RelatedArtistRepository relatedArtistRepository;
    private final CommonArtistService commonArtistService;
    private final SpotifyService spotifyService;

    public List<ArtistDetail> handleRelatedArtist(List<ArtistDetail> artistDetailList){
        // Check if the givens artists are not yet in related artist primary
        Set<String> uniqueArtistDetailsId = commonArtistService.extractIdsFromArtistDetails(artistDetailList);
        return null;
    }




}
