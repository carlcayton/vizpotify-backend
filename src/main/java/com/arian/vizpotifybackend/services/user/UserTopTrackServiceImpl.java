package com.arian.vizpotifybackend.services.user;

import com.arian.vizpotifybackend.dto.TrackDTO;
import com.arian.vizpotifybackend.enums.TimeRange;
import com.arian.vizpotifybackend.model.TrackDetail;
import com.arian.vizpotifybackend.model.UserTopTrack;
import com.arian.vizpotifybackend.repository.UserTopTrackRepository;
import com.arian.vizpotifybackend.services.redis.TrackCacheService;
import com.arian.vizpotifybackend.services.spotify.SpotifyService;
import com.arian.vizpotifybackend.services.track.TrackDetailService;
import com.arian.vizpotifybackend.services.user.util.TopItemUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

//
@Service
@RequiredArgsConstructor
public class UserTopTrackServiceImpl implements IUserTopItemService<TrackDTO, UserTopTrack> {
    private final UserTopTrackRepository userTopTrackRepository;
    private final SpotifyService spotifyService;
    private final TrackDetailService trackDetailService;
    private final TrackCacheService trackCacheService;

    @Override
    public Map<String, List<TrackDTO>> getUserTopItems(String userId) {

        boolean userExists =userTopTrackRepository.existsByUserSpotifyId(userId);
        if (userExists) {
            return fetchUserTopItemsFromDB(userId);
        } else {
            return null;
//            return fetchFromSpotifyAndStoreUserTopTracks(userSpotifyId);
        }
    }

    @Override
    public boolean storeUserTopItems(String userId, Map<String, List<UserTopTrack>> topItems) {
        return false;
    }


    @Override
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
                trackDetailsForUser.computeIfAbsent(timeRangeKey, k -> new ArrayList<>()).add(trackDTO);
            }
        }

        return trackDetailsForUser;
    }


    @Override
    public Map<String, List<TrackDTO>> fetchUserTopItemsFromSpotifyAndSave(String userId) {

        return null;
    }

    @Override
    public List<TrackDTO> processItemsForTimeRange(String timeRange, String userId, List<UserTopTrack> items) {
        return null;
    }

    @Override
    public UserTopTrack createTopItemObject(String userId, TrackDTO itemDTO, String timeRange, int rank) {
        return null;
    }
}
