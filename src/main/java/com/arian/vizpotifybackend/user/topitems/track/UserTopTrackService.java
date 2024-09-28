package com.arian.vizpotifybackend.user.topitems.track;

import com.arian.vizpotifybackend.common.SpotifyService;
import com.arian.vizpotifybackend.common.TimeRange;
import com.arian.vizpotifybackend.common.mapper.TrackMapper;
import com.arian.vizpotifybackend.track.TrackDto;
import com.arian.vizpotifybackend.track.TrackDetail;
import com.arian.vizpotifybackend.track.TrackDetailService;
import com.arian.vizpotifybackend.user.topitems.common.UserTopItemService;

import com.arian.vizpotifybackend.user.topitems.common.TopItemUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserTopTrackService implements UserTopItemService {
    private final UserTopTrackRepository userTopTrackRepository;
    private final SpotifyService spotifyService;
    private final TrackDetailService trackDetailService;
    private final TrackMapper trackMapper;

    public Map<String, List<TrackDto>> getUserTopItems(String userId) {

        boolean userExists =userTopTrackRepository.existsByUserSpotifyId(userId);
        if (userExists) {
            return fetchUserTopItemsFromDB(userId);
        } else {
            return fetchUserTopItemsFromSpotifyAndSave(userId);
        }
    }

    public Map<String, List<TrackDto>> fetchUserTopItemsFromDB(String userId) {
        Map<String, List<TrackDto>> trackDetailsForUser = new HashMap<>();
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
                TrackDto trackDto = trackMapper.trackDetailToTrackDto(trackDetail);
                String timeRangeKey = userTopTrack.getTimeRange();
                trackDetailsForUser.computeIfAbsent(TopItemUtil.formatTimeRangeForDto(timeRangeKey), k -> new ArrayList<>()).add(trackDto);
            }
        }

        return trackDetailsForUser;
    }


    public Map<String, List<TrackDto>> fetchUserTopItemsFromSpotifyAndSave(String userId) {
        Map<TimeRange, Paging<Track>> userTopTracksForAllTimeRange = spotifyService.getUserTopTracksForAllTimeRange(userId);

        Set<Track> allTracksAsSet =
                trackDetailService
                        .extractUniqueTracks(userTopTracksForAllTimeRange);
        trackDetailService.processAndStoreNewTrackDetails(allTracksAsSet);
        Map<String, List<TrackDto>> output = new HashMap<>();
        for (Map.Entry<TimeRange, Paging<Track>> entry : userTopTracksForAllTimeRange.entrySet()) {
            String currentTimeRange = entry.getKey().getValue();
            List<TrackDto> trackDtos = processTracksForTimeRange(currentTimeRange, userId, entry.getValue());
            output.put(TopItemUtil.formatTimeRangeForDto(currentTimeRange), trackDtos);
        }

        return output;

    }


    private List<TrackDto> processTracksForTimeRange(String timeRange, String spotifyId, Paging<Track> tracksPage) {
        List<TrackDto> trackDtos = new ArrayList<>();
        List<UserTopTrack> userTopTracks = new ArrayList<>();

        Track[] tracks = tracksPage.getItems();

        int rank = 1;
        for (Track track : tracks) {
            trackDtos.add(trackMapper.trackToTrackDto(track));
            UserTopTrack userTopTrack = createUserTopTrack(spotifyId, track.getId(), timeRange, rank++);
            userTopTracks.add(userTopTrack);
        }
        userTopTrackRepository.saveAll(userTopTracks);

        return trackDtos;
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
