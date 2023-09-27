package com.arian.vizpotifybackend.services.artist;

import com.arian.vizpotifybackend.dto.artist.ArtistDTO;
import com.arian.vizpotifybackend.model.ArtistDetail;
import com.arian.vizpotifybackend.model.RelatedArtist;
import com.arian.vizpotifybackend.model.composite.RelatedArtistId;
import com.arian.vizpotifybackend.repository.RelatedArtistRepository;
import com.arian.vizpotifybackend.services.spotify.SpotifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.Artist;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelatedArtistService {

    private final RelatedArtistRepository relatedArtistRepository;
    private final CommonArtistService commonArtistService;
    private final SpotifyService spotifyService;
    private final ArtistDetailService artistDetailService;


    public Set<ArtistDTO> getRelatedArtists(String artistId){
        List<ArtistDetail> relatedArtists =relatedArtistRepository.findRelatedArtistsByPrimaryArtistId(artistId);
        if(relatedArtists.size()>0){
            return relatedArtists.stream().map(artistDetailService::convertArtistDetailToArtistDTO).collect(Collectors.toSet());
        }else{
            return fetchFromSpotifyAndStoreRelatedArtists(artistId);
        }
    }
    public Set<ArtistDTO> fetchFromSpotifyAndStoreRelatedArtists(String artistId){
        Artist[] artists = spotifyService.getRelatedArtists(artistId);
        Set<Artist> artistSet = Arrays.stream(artists).collect(Collectors.toSet());
        artistDetailService.processAndStoreNewArtistDetails(artistSet);

        return Arrays.stream(artists)
                .map(artistDetailService::convertArtistToArtistDTO)
                .collect(Collectors.toSet());
    }
//    private boolean hasRelatedArtists(String spotifyId){
//
//    }





}
