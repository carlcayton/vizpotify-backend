
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
        return userTopTrackRepository.existsByUserSpotifyId(userId)
                ? fetchUserTopItemsFromDB(userId)
                : fetchUserTopItemsFromSpotifyAndSave(userId);
    }

    public Map<String, List<TrackDto>> fetchUserTopItemsFromDB(String userId) {
        List<UserTopTrack> allUserTopTracks = userTopTrackRepository.findByUserSpotifyId(userId);
        List<String> trackIds = allUserTopTracks.stream()
                .map(userTopTrack -> userTopTrack.getTrackDetail().getId())
                .collect(Collectors.toList());

        Map<String, TrackDetail> trackIdToDetailMap = trackDetailService.getTracksByIds(trackIds).stream()
                .collect(Collectors.toMap(TrackDetail::getId, track -> track));

        return allUserTopTracks.stream()
                .collect(Collectors.groupingBy(
                        userTopTrack -> TopItemUtil.formatTimeRangeForDto(userTopTrack.getTimeRange()),
                        Collectors.mapping(userTopTrack -> {
                            TrackDetail trackDetail = trackIdToDetailMap.get(userTopTrack.getTrackDetail().getId());
                            return trackMapper.trackDetailToTrackDto(trackDetail);
                        }, Collectors.toList())
                ));
    }

    public Map<String, List<TrackDto>> fetchUserTopItemsFromSpotifyAndSave(String userId) {
        Map<TimeRange, Paging<Track>> userTopTracksForAllTimeRange = spotifyService.getUserTopTracksForAllTimeRange(userId);

        Set<Track> allTracksAsSet = trackDetailService.extractUniqueTracks(userTopTracksForAllTimeRange);
        trackDetailService.processAndStoreNewTrackDetails(allTracksAsSet);

        return userTopTracksForAllTimeRange.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> TopItemUtil.formatTimeRangeForDto(entry.getKey().getValue()),
                        entry -> processTracksForTimeRange(entry.getKey().getValue(), userId, entry.getValue())
                ));
    }

    private List<TrackDto> processTracksForTimeRange(String timeRange, String spotifyId, Paging<Track> tracksPage) {
        List<UserTopTrack> userTopTracks = new ArrayList<>();
        List<TrackDto> trackDtos = Arrays.stream(tracksPage.getItems())
                .map(track -> {
                    TrackDto trackDto = trackMapper.trackToTrackDto(track);
                    userTopTracks.add(createUserTopTrack(spotifyId, track.getId(), timeRange, userTopTracks.size() + 1));
                    return trackDto;
                })
                .collect(Collectors.toList());

        userTopTrackRepository.saveAll(userTopTracks);
        return trackDtos;
    }

    private UserTopTrack createUserTopTrack(String spotifyId, String trackId, String timeRange, int rank) {
        return UserTopTrack.builder()
                .userSpotifyId(spotifyId)
                .trackDetail(TrackDetail.builder().id(trackId).build())
                .timeRange(timeRange)
                .rank(rank)
                .lastUpdated(new Date())
                .build();
    }
}