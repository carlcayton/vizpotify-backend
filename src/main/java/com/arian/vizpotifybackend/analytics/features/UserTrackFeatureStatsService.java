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

    @Transactional
    public UserTrackFeatureStatsMapDto fetchUserTrackFeatureStats(String spotifyId) {
        if (!userTrackFeatureStatsRepository.existsByUserSpotifyId(spotifyId)) {
            aggregateAndUpsertUserTrackFeatureStats(spotifyId);
        }
    
        List<UserTrackFeatureStats> userTrackFeatureStats = userTrackFeatureStatsRepository.findAllByUserSpotifyId(spotifyId);
        return userTrackFeatureStatsMapper.toMapDto(spotifyId, userTrackFeatureStats);
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
        List<String> trackIds = topTrackRepository.findTrackIdsByUserSpotifyIdAndTimeRange(spotifyUserId, timeRange);
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
