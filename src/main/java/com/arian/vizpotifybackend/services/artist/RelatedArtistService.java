package com.arian.vizpotifybackend.services.artist;

import com.arian.vizpotifybackend.dto.ArtistDTO;
import com.arian.vizpotifybackend.mapper.ArtistMapper;
import com.arian.vizpotifybackend.services.redis.ArtistCacheService;
import com.arian.vizpotifybackend.services.spotify.SpotifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.Artist;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelatedArtistService {

    private final ArtistCacheService artistCacheService;
    private final SpotifyService spotifyService;
    private final ArtistDetailService artistDetailService;
    private final ArtistMapper artistMapper;

    public List<ArtistDTO> getRelatedArtists(String artistId){
        Optional<List<ArtistDTO>> cachedRelatedArtists = artistCacheService.getRelatedArtistsFromCache(artistId);

        if (cachedRelatedArtists.isPresent() && !cachedRelatedArtists.get().isEmpty()) {
            return cachedRelatedArtists.get();
        } else {
            return fetchFromSpotifyAndStoreRelatedArtists(artistId);
        }
    }

    public List<ArtistDTO> fetchFromSpotifyAndStoreRelatedArtists(String artistId){
        List<ArtistDTO> relatedArtists = fetchFromSpotify(artistId);
        artistCacheService.cacheRelatedArtists(artistId, relatedArtists);
        return relatedArtists;
    }

    // update this method such that it uses the mapstruct mapper to convert the Artist to ArtistDTO


    private List<ArtistDTO> fetchFromSpotify(String artistId){
        Artist[] artists = spotifyService.getRelatedArtists(artistId);
        return Arrays.stream(artists)
                .map(artistMapper::artistToArtistDTOForRelatedArtists)
                .collect(Collectors.toList());
    }
}
