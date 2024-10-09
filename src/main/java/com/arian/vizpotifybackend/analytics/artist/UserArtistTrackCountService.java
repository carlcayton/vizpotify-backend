package com.arian.vizpotifybackend.analytics.artist;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserArtistTrackCountService {
    private final UserArtistTrackCountRepository userArtistTrackCountRepository;
    private final UserArtistTrackCountMapper userArtistTrackCountMapper;

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
                        userArtistTrackCountRepository.aggregateAndInsertUserArtistTrackCount(spotifyUserId);
                    }
                });
    }

    private boolean isCountOutdated(UserArtistTrackCount count) {
        return count.getUpdatedAt().plusDays(7).isBefore(LocalDateTime.now());
    }
}
