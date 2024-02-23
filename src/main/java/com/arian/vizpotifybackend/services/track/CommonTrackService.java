package com.arian.vizpotifybackend.services.track;

import com.arian.vizpotifybackend.repository.TrackDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommonTrackService {

    private final TrackDetailRepository trackDetailRepository;

    public Set<Track> extractTrackNotInTrackTable(Set<Track> tracks) {
        Set<String> trackIds = new HashSet<>();
        for (Track track : tracks) {
            trackIds.add(track.getId());
        }
        List<String> existingTracks = trackDetailRepository.findExistingIds(trackIds);

        existingTracks.forEach(trackIds::remove);

        return tracks.stream()
                .filter(track -> trackIds.contains(track.getId()))
                .collect(Collectors.toSet());
    }
}
