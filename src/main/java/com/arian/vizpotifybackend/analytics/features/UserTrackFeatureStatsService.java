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
                .tempo(calculateAverage(features, AudioFeature::getTempo))
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
