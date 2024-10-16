package com.arian.vizpotifybackend.analytics.features;

import com.arian.vizpotifybackend.analytics.AnalyticsResponse;
import com.arian.vizpotifybackend.track.AudioFeature;
import com.arian.vizpotifybackend.track.AudioFeatureRepository;
import com.arian.vizpotifybackend.user.topitems.track.UserTopTrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserTrackFeatureStatsService {
    private final UserTopTrackRepository userTopTrackRepository;
    private final AudioFeatureRepository audioFeatureRepository;
    private final UserTrackFeatureStatsRepository userTrackFeatureStatsRepository;
    private final UserTrackFeatureStatsMapper userTrackFeatureStatsMapper;

    @Transactional
    @Retryable(maxAttempts = 12, backoff = @Backoff(delay = 5000))
    public AnalyticsResponse<UserTrackFeatureStatsMapDto> fetchUserTrackFeatureStats(String spotifyId) {
        List<String> trackIds = userTopTrackRepository.findTrackIdsByUserSpotifyIdAndTimeRange(spotifyId, "long_term");
        if (trackIds.isEmpty()) {
            return new AnalyticsResponse<>("processing", "Data is being processed. Please try again later.", null);
        }
        aggregateAndUpsertUserTrackFeatureStats(spotifyId);
        List<UserTrackFeatureStats> userTrackFeatureStats = userTrackFeatureStatsRepository.findAllByUserSpotifyId(spotifyId);
        UserTrackFeatureStatsMapDto dto = userTrackFeatureStatsMapper.toMapDto(spotifyId, userTrackFeatureStats);
        return new AnalyticsResponse<>("complete", "Data processing complete", dto);
    }

    @Transactional
    public void aggregateAndUpsertUserTrackFeatureStats(String spotifyUserId) {
        Stream.of("short_term", "medium_term", "long_term")
                .forEach(timeRange -> {
                    UserTrackFeatureStats stats = calculateStats(spotifyUserId, timeRange);
                    Optional<UserTrackFeatureStats> existingStats = userTrackFeatureStatsRepository.findByUserSpotifyIdAndTimeRange(spotifyUserId, timeRange);

                    stats.setCreatedAt(existingStats.map(UserTrackFeatureStats::getCreatedAt).orElse(LocalDateTime.now()));
                    stats.setUpdatedAt(LocalDateTime.now());

                    userTrackFeatureStatsRepository.save(stats);
                });
    }
    private UserTrackFeatureStats calculateStats(String spotifyUserId, String timeRange) {
        List<String> trackIds = userTopTrackRepository.findTrackIdsByUserSpotifyIdAndTimeRange(spotifyUserId, timeRange);
        List<AudioFeature> features = audioFeatureRepository.findAllById(trackIds);

        return createUserTrackFeatureStats(spotifyUserId, timeRange, features);
    }

    private UserTrackFeatureStats createUserTrackFeatureStats(String spotifyUserId, String timeRange, List<AudioFeature> features) {
        return UserTrackFeatureStats.builder()
                .userSpotifyId(spotifyUserId)
                .timeRange(timeRange)
                .acousticness(calculateAverage(features, AudioFeature::getAcousticness))
                .danceability(calculateAverage(features, AudioFeature::getDanceability))
                .energy(calculateAverage(features, AudioFeature::getEnergy))
                .instrumentalness(calculateAverage(features, AudioFeature::getInstrumentalness))
                .liveness(calculateAverage(features, AudioFeature::getLiveness))
                .speechiness(calculateAverage(features, AudioFeature::getSpeechiness))
                .valence(calculateAverage(features, AudioFeature::getValence))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private double calculateAverage(List<AudioFeature> features, java.util.function.ToDoubleFunction<AudioFeature> featureExtractor) {
        return features.stream()
                .mapToDouble(featureExtractor)
                .average()
                .orElse(0.0);
    }

}
