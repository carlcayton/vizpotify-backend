
package com.arian.vizpotifybackend.artist;

import com.arian.vizpotifybackend.cache.ArtistCacheService;
import com.arian.vizpotifybackend.common.SpotifyService;
import com.arian.vizpotifybackend.common.mapper.ArtistMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.Artist;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelatedArtistService {

    private final ArtistCacheService artistCacheService;
    private final SpotifyService spotifyService;
    private final ArtistMapper artistMapper;

    public List<ArtistDto> getRelatedArtists(String artistId){
        Optional<List<ArtistDto>> cachedRelatedArtists = artistCacheService.getRelatedArtistsFromCache(artistId);

        if (cachedRelatedArtists.isPresent() && !cachedRelatedArtists.get().isEmpty()) {
            return cachedRelatedArtists.get();
        } else {
            return fetchFromSpotifyAndStoreRelatedArtists(artistId);
        }
    }

    public List<ArtistDto> fetchFromSpotifyAndStoreRelatedArtists(String artistId){
        List<ArtistDto> relatedArtists = fetchFromSpotify(artistId);
        artistCacheService.cacheRelatedArtists(artistId, relatedArtists);
        return relatedArtists;
    }

    private List<ArtistDto> fetchFromSpotify(String artistId){
        Artist[] artists = spotifyService.getRelatedArtists(artistId);
        return Arrays.stream(artists).map(artistMapper::artistToArtistDto)
                .collect(Collectors.toList());
    }
}
