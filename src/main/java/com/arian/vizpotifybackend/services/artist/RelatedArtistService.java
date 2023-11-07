package com.arian.vizpotifybackend.services.artist;

import com.arian.vizpotifybackend.dto.artist.ArtistDTO;
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

    public Set<ArtistDTO> getRelatedArtists(String artistId){
        // Attempt to retrieve cached related artists
        Optional<List<ArtistDTO>> cachedRelatedArtists = artistCacheService.getRelatedArtistsFromCache(artistId);

        if (cachedRelatedArtists.isPresent() && !cachedRelatedArtists.get().isEmpty()) {
            // Return the cached related artists if present
            return new HashSet<>(cachedRelatedArtists.get());
        } else {
            // If not present in cache, fetch from Spotify, cache result, and return
            return fetchFromSpotifyAndStoreRelatedArtists(artistId);
        }
    }

    public Set<ArtistDTO> fetchFromSpotifyAndStoreRelatedArtists(String artistId){
        // Fetch from Spotify
        Set<ArtistDTO> relatedArtists = fetchFromSpotify(artistId);
        // Store fetched results in cache
        artistCacheService.cacheRelatedArtists(artistId, new ArrayList<>(relatedArtists));
        // Return the fetched results
        return relatedArtists;
    }

    private Set<ArtistDTO> fetchFromSpotify(String artistId){
        // Fetch related artists from Spotify
        Artist[] artists = spotifyService.getRelatedArtists(artistId);
        // Convert to DTO and collect into a set
        return Arrays.stream(artists)
                .map(artistDetailService::convertArtistToArtistDTOForRelatedArtists)
                .collect(Collectors.toSet());
    }
}
