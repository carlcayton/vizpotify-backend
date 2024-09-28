
package com.arian.vizpotifybackend.artist;


import com.arian.vizpotifybackend.cache.TrackCacheService;
import com.arian.vizpotifybackend.common.SpotifyService;
import com.arian.vizpotifybackend.common.mapper.TrackMapper;
import com.arian.vizpotifybackend.track.TrackDto;
import com.arian.vizpotifybackend.track.TrackDetailRepository;
import com.arian.vizpotifybackend.track.TrackDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtistTopTracksService {
    private final TrackCacheService trackCacheService;
    private final TrackDetailService trackDetailService;
    private final TrackMapper trackMapper;
    private final SpotifyService spotifyService;

    public List<TrackDto> getArtistTopTracks(String artistId) {
        Optional<List<TrackDto>> cachedTopTracks = trackCacheService.getArtistTopTracksFromCache(artistId);

        if (cachedTopTracks.isPresent() && !cachedTopTracks.get().isEmpty()) {
            return cachedTopTracks.get();
        } else {
            return fetchFromSpotifyAndStoreTopTracks(artistId);
        }
    }

    public List<TrackDto> fetchFromSpotifyAndStoreTopTracks(String artistId) {
        Track[] tracks = getArtistTopTracksFromSpotify(artistId);
        if (tracks == null) {
            return Collections.emptyList();
        }
        List<TrackDto> trackDtos = Arrays.stream(tracks)
                .map(trackMapper::trackToTrackDto)
                .limit(5)
                .collect(Collectors.toList());
        trackCacheService.cacheArtistTopTracks(artistId, trackDtos);
        return trackDtos;
    }

    private Track[] getArtistTopTracksFromSpotify(String artistId) {
        return spotifyService.getArtistTopTracks(artistId);
    }
}
