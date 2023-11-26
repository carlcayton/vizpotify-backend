package com.arian.vizpotifybackend.services.user;

import com.arian.vizpotifybackend.dto.TrackDTO;
import com.arian.vizpotifybackend.enums.TimeRange;
import com.arian.vizpotifybackend.model.TrackDetail;
import com.arian.vizpotifybackend.model.UserTopTrack;
import com.arian.vizpotifybackend.repository.UserTopTrackRepository;
import com.arian.vizpotifybackend.services.spotify.SpotifyService;
import com.arian.vizpotifybackend.services.track.TrackDetailService;
import com.arian.vizpotifybackend.services.user.util.TopItemUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import se.michaelthelin.spotify.model_objects.specification.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserTopTrackServiceImpl {
    private final UserTopTrackRepository userTopTrackRepository;
    private final SpotifyService spotifyService;
    private final TrackDetailService trackDetailService;

    public Map<String, List<TrackDTO>> getUserTopItems(String userId) {

        boolean userExists =userTopTrackRepository.existsByUserSpotifyId(userId);
        if (userExists) {
            return fetchUserTopItemsFromDB(userId);
        } else {
            return fetchUserTopItemsFromSpotifyAndSave(userId);
        }
    }

    public Map<String, List<TrackDTO>> fetchUserTopItemsFromDB(String userId) {
        Map<String, List<TrackDTO>> trackDetailsForUser = new HashMap<>();
        List<UserTopTrack> allUserTopTracks = userTopTrackRepository.findByUserSpotifyId(userId);

        List<String> trackIds = allUserTopTracks.stream()
                .map(UserTopTrack::getTrackId)
                .collect(Collectors.toList());

        List<TrackDetail> trackDetailsList = trackDetailService.getTracksByIds(trackIds);

        Map<String, TrackDetail> trackIdToDetailMap = new HashMap<>();
        for (TrackDetail detail : trackDetailsList) {
            trackIdToDetailMap.put(detail.getId(), detail);
        }

        for (UserTopTrack userTopTrack : allUserTopTracks) {
            TrackDetail trackDetail = trackIdToDetailMap.get(userTopTrack.getTrackId());
            if (trackDetail != null) {
                TrackDTO trackDTO = trackDetailService.convertTrackDetailToTrackDTO(trackDetail);
                String timeRangeKey = userTopTrack.getTimeRange();
                trackDetailsForUser.computeIfAbsent(TopItemUtil.formatTimeRangeForDTO(timeRangeKey), k -> new ArrayList<>()).add(trackDTO);
            }
        }

        return trackDetailsForUser;
    }


    public Map<String, List<TrackDTO>> fetchUserTopItemsFromSpotifyAndSave(String userId) {
        Map<TimeRange, Paging<Track>> userTopTracksForAllTimeRange = spotifyService.getUserTopTracksForAllTimeRange(userId);

        Set<Track> allTracksAsSet =
                trackDetailService
                        .extractUniqueTracks(userTopTracksForAllTimeRange);
        trackDetailService.processAndStoreNewTrackDetails(allTracksAsSet);
        Map<String, List<TrackDTO>> output = new HashMap<>();
        for (Map.Entry<TimeRange, Paging<Track>> entry : userTopTracksForAllTimeRange.entrySet()) {
            String currentTimeRange = entry.getKey().getValue();
            List<TrackDTO> trackDTOs = processTracksForTimeRange(currentTimeRange, userId, entry.getValue());
            output.put(TopItemUtil.formatTimeRangeForDTO(currentTimeRange), trackDTOs);
        }

        return output;

    }
    private List<TrackDTO> processTracksForTimeRange(String timeRange, String spotifyId, Paging<Track> tracksPage) {
        List<TrackDTO> trackDTOs = new ArrayList<>();
        List<UserTopTrack> userTopTracks = new ArrayList<>();

        Track[] tracks = tracksPage.getItems();

        int rank = 1;
        for (Track track : tracks) {
            trackDTOs.add(trackDetailService.convertTrackToTrackDTO(track));
            UserTopTrack userTopTrack = createUserTopTrack(spotifyId, track.getId(), timeRange, rank++);
            userTopTracks.add(userTopTrack);
        }
        userTopTrackRepository.saveAll(userTopTracks);

        return trackDTOs;
    }

    private UserTopTrack createUserTopTrack(String spotifyId, String trackId, String timeRange, int rank) {
        return UserTopTrack.builder()
                .userSpotifyId(spotifyId)
                .trackId(trackId)
                .timeRange(timeRange)
                .rank(rank)
                .lastUpdated(new Date())
                .build();
    }
}
