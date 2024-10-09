package com.arian.vizpotifybackend.analytics.artist;

import com.arian.vizpotifybackend.user.topitems.track.UserTopTrack;
import com.arian.vizpotifybackend.user.topitems.track.UserTopTrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserArtistTrackCountService {
    private final UserArtistTrackCountRepository userArtistTrackCountRepository;
    private final UserArtistTrackCountMapper userArtistTrackCountMapper;
    private final UserTopTrackRepository userTopTrackRepository;

    @Transactional
    public UserArtistTrackCountMapDto fetchUserArtistTrackCount(String spotifyId) {
        aggregateAndUpsertUserArtistTrackCount(spotifyId);
        List<UserArtistTrackCount> userArtistTrackCounts = userArtistTrackCountRepository.findAllByUserSpotifyId(spotifyId);
        return userArtistTrackCountMapper.toMapDto(spotifyId, userArtistTrackCounts);
    }

    @Transactional
    public void aggregateAndUpsertUserArtistTrackCount(String spotifyUserId) {
        Stream.of("short_term", "medium_term", "long_term")
                .forEach(timeRange -> {
                    Optional<UserArtistTrackCount> existingCount = userArtistTrackCountRepository
                            .findFirstByUserSpotifyIdAndTimeRangeOrderByUpdatedAtDesc(spotifyUserId, timeRange);

                    if (existingCount.isEmpty() || isCountOutdated(existingCount.get())) {
                        userArtistTrackCountRepository.deleteByUserSpotifyIdAndTimeRange(spotifyUserId, timeRange);
                        List<UserArtistTrackCount> newCounts = calculateArtistTrackCounts(spotifyUserId, timeRange);
                        userArtistTrackCountRepository.saveAll(newCounts);
                    }
                });
    }

    private boolean isCountOutdated(UserArtistTrackCount count) {
        return count.getUpdatedAt().plusDays(7).isBefore(LocalDateTime.now());
    }

    private List<UserArtistTrackCount> calculateArtistTrackCounts(String spotifyUserId, String timeRange) {
        List<UserTopTrack> userTopTracks = userTopTrackRepository.findByUserSpotifyIdAndTimeRangeWithTrackDetails(spotifyUserId, timeRange);
        
        Map<String, Integer> artistTrackCounts = new HashMap<>();
        for (UserTopTrack userTopTrack : userTopTracks) {
            String[] artists = userTopTrack.getTrackId().split(",");
            for (String artist : artists) {
                artistTrackCounts.merge(artist.trim(), 1, Integer::sum);
            }
        }

        int totalTracks = userTopTracks.size();
        return artistTrackCounts.entrySet().stream()
                .map(entry -> UserArtistTrackCount.builder()
                        .userSpotifyId(spotifyUserId)
                        .timeRange(timeRange)
                        .artistName(entry.getKey())
                        .trackCount(entry.getValue())
                        .percentage((entry.getValue() * 100.0) / totalTracks)
                        .build())
                .collect(Collectors.toList());
    }
}
