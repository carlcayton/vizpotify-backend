package com.arian.vizpotifybackend.track;

import com.arian.vizpotifybackend.common.TimeRange;
import com.arian.vizpotifybackend.common.mapper.TrackMapper;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrackDetailService {

    private final TrackDetailRepository trackDetailRepository;
    private final CommonTrackService commonTrackService;
    private final AudioFeatureService audioFeatureService;
    private final TrackMapper trackMapper;


    @Transactional
    public void processAndStoreNewTrackDetails(Set<Track> allTrackDetails) {
        Set<Track> tracksNotInTable = commonTrackService.extractTrackNotInTrackTable(allTrackDetails);
        Set<TrackDetail> newTracks = tracksNotInTable.stream()
                .map(trackMapper::trackToTrackDetail)
                .collect(Collectors.toSet());

        trackDetailRepository.saveAll(newTracks);
        List<String> trackIds = newTracks.stream()
                .map(TrackDetail::getId)
                .toList();
       audioFeatureService.saveAudioFeaturesForSeveralTracks(trackIds);
    }

    public List<TrackDetail> getTracksByIds(List<String> ids) {
        return trackDetailRepository.findByIdIn(ids);
    }

    public Set<Track> extractUniqueTracks(Map<TimeRange, Paging<Track>> trackPagingMap) {
        Set<Track> allUniqueTracks = new HashSet<>();
        for (Map.Entry<TimeRange, Paging<Track>> entry : trackPagingMap.entrySet()) {
            Track[] tracks = entry.getValue().getItems();
            allUniqueTracks.addAll(Arrays.asList(tracks));
        }
        return allUniqueTracks;
    }


}
