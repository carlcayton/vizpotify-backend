package com.arian.vizpotifybackend.services.artist;


import com.arian.vizpotifybackend.dto.TrackDTO;
import com.arian.vizpotifybackend.services.redis.TrackCacheService;
import com.arian.vizpotifybackend.services.spotify.SpotifyService;
import com.arian.vizpotifybackend.services.track.TrackDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtistTopTracksService {
    private final TrackCacheService trackCacheService;
    private final SpotifyService spotifyService;
    private final TrackDetailService trackDetailService;

    public List<TrackDTO> getArtistTopTracks(String artistId) {
        Optional<List<TrackDTO>> cachedTopTracks = trackCacheService.getArtistTopTracksFromCache(artistId);

        if (cachedTopTracks.isPresent() && !cachedTopTracks.get().isEmpty()) {
            return cachedTopTracks.get();
        } else {
            return fetchFromSpotifyAndStoreTopTracks(artistId);
        }
    }

    public List<TrackDTO> fetchFromSpotifyAndStoreTopTracks(String artistId) {
        Track[] tracks = getArtistTopTracksFromSpotify(artistId);
        if (tracks == null) {
            return Collections.emptyList();
        }
        List<TrackDTO> trackDTOs = Arrays.stream(tracks)
                .map(trackDetailService::convertTrackToTrackDTO)
                .limit(5)
                .collect(Collectors.toList());
        trackCacheService.cacheArtistTopTracks(artistId, trackDTOs);
        return trackDTOs;
    }

    private Track[] getArtistTopTracksFromSpotify(String artistId) {
        return spotifyService.getArtistTopTracks(artistId);
    }
}
