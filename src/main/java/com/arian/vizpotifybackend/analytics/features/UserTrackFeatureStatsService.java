package com.arian.vizpotifybackend.analytics.features;

import com.arian.vizpotifybackend.track.AudioFeature;
import com.arian.vizpotifybackend.track.AudioFeatureRepository;
import com.arian.vizpotifybackend.user.topitems.track.UserTopTrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserTrackFeatureStatsService {
    private final UserTopTrackRepository topTrackRepository;
    private final AudioFeatureRepository audioFeatureRepository;
    private final UserTrackFeatureStatsRepository userTrackFeatureStatsRepository;
    private final UserTrackFeatureStatsMapper userTrackFeatureStatsMapper;

    public UserTrackFeatureStatsMapDto fetchUserTrackFeatureStats(String spotifyId) {
        List<UserTrackFeatureStats> userTrackFeatureStats = userTrackFeatureStatsRepository.findAllByUserSpotifyId(spotifyId);
        Map<String, UserTrackFeatureStatsDto> featureStatsByTimeRange = userTrackFeatureStats.stream()
                .collect(java.util.stream.Collectors.toMap(
                        UserTrackFeatureStats::getTimeRange,
                        userTrackFeatureStatsMapper::toDto
                ));
        return new UserTrackFeatureStatsMapDto(spotifyId, featureStatsByTimeRange);
    }

    @Transactional
    public void aggregateAndUpsertUserTrackFeatureStats(String spotifyUserId) {
        Optional<UserTrackFeatureStats> statsOptional = userTrackFeatureStatsRepository.findByUserSpotifyId(spotifyUserId);

        if (statsOptional.isEmpty() ||
                statsOptional.get().getUpdatedAt().isBefore(LocalDateTime.now().minusDays(9))) {

            userTrackFeatureStatsRepository.deleteByUserSpotifyId(spotifyUserId);

            Stream.of("short_term", "medium_term", "long_term")
                    .forEach(timeRange -> {
                        UserTrackFeatureStats stats = calculateStats(spotifyUserId, timeRange);
                        stats.setCreatedAt(LocalDateTime.now());
                        stats.setUpdatedAt(LocalDateTime.now());

                        userTrackFeatureStatsRepository.save(stats);
                    });
        }
    }

    // ... (rest of the methods remain the same)
}
