
package com.arian.vizpotifybackend.analytics.core;

import com.arian.vizpotifybackend.analytics.artist.UserArtistTrackCountRepository;
import com.arian.vizpotifybackend.analytics.artist.UserTrackFeatureStatsMapper;
import com.arian.vizpotifybackend.analytics.era.UserMusicEraSummaryRepository;
import com.arian.vizpotifybackend.analytics.features.UserTrackFeatureStats;
import com.arian.vizpotifybackend.analytics.features.UserTrackFeatureStatsDto;
import com.arian.vizpotifybackend.analytics.features.UserTrackFeatureStatsRepository;
import com.arian.vizpotifybackend.analytics.genre.UserGenreDistribution;
import com.arian.vizpotifybackend.analytics.genre.UserGenreDistributionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsAggregator {

    private final UserTrackFeatureStatsRepository userTrackFeatureStatsRepository;
    private final UserGenreDistributionRepository userGenreDistributionRepository;
    private final UserMusicEraSummaryRepository userMusicEraSummaryRepository;
    private final UserArtistTrackCountRepository userArtistTrackCountRepository;

    private final UserTrackFeatureStatsMapper userTrackFeatureStatsMapper;

    @Transactional
    public void aggregateAndInsertUserTrackFeatureStats(String spotifyId) {
        userTrackFeatureStatsRepository.aggregateAndInsertUserTrackFeatureStats(spotifyId);
    }

    public Map<String, UserTrackFeatureStatsDto> fetchUserTrackFeatureStats(String spotifyId) {
        List<UserTrackFeatureStats> userTrackFeatureStats = userTrackFeatureStatsRepository.findAllByUserSpotifyId(spotifyId);
        return groupByTimeRange(userTrackFeatureStats, UserTrackFeatureStats::getTimeRange, userTrackFeatureStatsMapper::toDto);
    }

    private static <T, R> Map<String, R> groupByTimeRange(
            List<T> input,
            Function<T, String> timeRangeExtractor,
            Function<T, R> mapper
    ) {
        return input.stream()
                .collect(Collectors.toMap(
                        timeRangeExtractor,
                        mapper,
                        (existing, replacement) -> existing
                ));
    }

    public List<UserGenreDistribution> fetchTopNUserGenreDistribution(String spotifyUserId, int maxSize) {
        List<UserGenreDistribution> allGenres =
                userGenreDistributionRepository.findByUserSpotifyIdOrderByPercentageDesc(spotifyUserId);
        Map<String, List<UserGenreDistribution>> genresByTimeRange = allGenres.stream()
                .collect(Collectors.groupingBy(UserGenreDistribution::getTimeRange));

        return genresByTimeRange.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .limit(maxSize))
                .collect(Collectors.toList());
    }


    public void aggregateAndStoreGenreDistributionForUser(String spotifyId) {
        userGenreDistributionRepository.aggregateAndInsertUserGenreDistribution(spotifyId);
    }


    public void aggregateAndStoreMusicEraSummary(String spotifyId) {
        userMusicEraSummaryRepository.aggregateAndInsertUserMusicEraSummary(spotifyId);
    }



    private String toCamelCase(String snakeStr) {
        String[] parts = snakeStr.split("_");
        StringBuilder camelCaseString = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            camelCaseString.append(Character.toUpperCase(parts[i].charAt(0)))
                    .append(parts[i].substring(1));
        }
        return camelCaseString.toString();
    }

    private List<Map<String, Object>> ensureAllErasPresent(List<Map<String, Object>> results) {
        List<String> allEras = Arrays.asList("1950s", "1960s", "1970s", "1980s", "1990s", "2000s", "2010s", "2020s", "< 1950");
        Map<String, Map<String, Object>> resultsByEra = results.stream()
                .collect(Collectors.toMap(
                        result -> (String) result.get("releaseDateRange"),
                        result -> result
                ));

        return allEras.stream()
                .map(era -> resultsByEra.computeIfAbsent(era, k -> createDefaultEraResult(k)))
                .collect(Collectors.toList());
    }

    private Map<String, Object> createDefaultEraResult(String era) {
        Map<String, Object> defaultResult = new HashMap<>();
        defaultResult.put("releaseDateRange", era);
        defaultResult.put("trackCount", 0);
        defaultResult.put("percentage", BigDecimal.ZERO);
        return defaultResult;
    }
    @Transactional
    public AnalyticsDto getAllAnalyticsForUser(String userId, boolean analyticsAvailable) {
        if (!analyticsAvailable) {
            aggregateAndInsertUserTrackFeatureStats(userId);
            aggregateAndStoreGenreDistributionForUser(userId);
            aggregateAndStoreMusicEraSummary(userId);
        }

        AnalyticsDto analyticsDto = new AnalyticsDto(
                fetchUserTrackFeatureStats(userId)
        );

        return analyticsDto;
    }
}