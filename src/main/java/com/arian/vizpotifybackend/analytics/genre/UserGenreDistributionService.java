package com.arian.vizpotifybackend.analytics.genre;

import com.arian.vizpotifybackend.analytics.AnalyticsResponse;
import com.arian.vizpotifybackend.user.topitems.artist.UserTopArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserGenreDistributionService {
    private final UserTopArtistRepository userTopArtistRepository;
    private final UserGenreDistributionRepository userGenreDistributionRepository;
    private final UserGenreDistributionMapper userGenreDistributionMapper;


    @Transactional
    @Retryable(maxAttempts = 12, backoff = @Backoff(delay = 5000))
    public AnalyticsResponse<UserGenreDistributionMapDto> fetchUserGenreDistribution(String spotifyId) {
        List<Object[]> genreData = userTopArtistRepository.findGenresAndCountByUserSpotifyId(spotifyId);
        if (genreData.isEmpty()) {
            return new AnalyticsResponse<>("processing", "Data is being processed. Please try again later.", null);
        }
        aggregateAndUpsertUserGenreDistribution(spotifyId);
        List<UserGenreDistribution> userGenreDistributions = userGenreDistributionRepository.findByUserSpotifyIdOrderByPercentageDesc(spotifyId);
        UserGenreDistributionMapDto dto = userGenreDistributionMapper.toMapDto(spotifyId, userGenreDistributions);
        return new AnalyticsResponse<>("complete", "Data processing complete", dto);
    }


    @Transactional
    public void aggregateAndUpsertUserGenreDistribution(String spotifyUserId) {
        Stream.of("short_term", "medium_term", "long_term")
                .forEach(timeRange -> {
                    Optional<UserGenreDistribution> existingDistribution = userGenreDistributionRepository
                            .findFirstByUserSpotifyIdAndTimeRangeOrderByUpdatedAtDesc(spotifyUserId, timeRange);

                    if (existingDistribution.isEmpty() || isDistributionOutdated(existingDistribution.get())) {
                        List<GenreDistributionDto> genreDistributions = calculateGenreDistribution(spotifyUserId, timeRange);
                        userGenreDistributionRepository.deleteByUserSpotifyIdAndTimeRange(spotifyUserId, timeRange);
                        saveGenreDistributions(spotifyUserId, timeRange, genreDistributions);
                    }
                });
    }

    private boolean isDistributionOutdated(UserGenreDistribution distribution) {
        return distribution.getUpdatedAt().plusDays(7).isBefore(LocalDateTime.now());
    }


    public void saveGenreDistributions(String spotifyUserId, String timeRange, List<GenreDistributionDto> genreDistributions) {
        List<UserGenreDistribution> existingDistributions =
                userGenreDistributionRepository.findByUserSpotifyIdAndTimeRange(spotifyUserId, timeRange);

        Map<String, UserGenreDistribution> existingDistributionMap = existingDistributions.stream()
                .collect(Collectors.toMap(
                        distribution -> distribution.getGenre(),
                        distribution -> distribution
                ));

        List<UserGenreDistribution> distributionsToSave = new ArrayList<>();

        for (GenreDistributionDto dto : genreDistributions) {
            UserGenreDistribution distribution = existingDistributionMap.getOrDefault(dto.genre(),
                    UserGenreDistribution.builder()
                            .userSpotifyId(spotifyUserId)
                            .timeRange(timeRange)
                            .genre(dto.genre())
                            .createdAt(LocalDateTime.now())
                            .build());

            distribution.setGenreCount(dto.genreCount());
            distribution.setPercentage(dto.percentage());
            distribution.setUpdatedAt(LocalDateTime.now());

            distributionsToSave.add(distribution);
        }

        userGenreDistributionRepository.saveAll(distributionsToSave);

        // Remove old distributions that are no longer present
        Set<String> currentGenres = genreDistributions.stream()
                .map(GenreDistributionDto::genre)
                .collect(Collectors.toSet());

        List<UserGenreDistribution> distributionsToDelete = existingDistributions.stream()
                .filter(distribution -> !currentGenres.contains(distribution.getGenre()))
                .collect(Collectors.toList());

        if (!distributionsToDelete.isEmpty()) {
            userGenreDistributionRepository.deleteAll(distributionsToDelete);
        }
    }


    private List<GenreDistributionDto> calculateGenreDistribution(String spotifyUserId, String timeRange) {
        List<Object[]> genreData = userTopArtistRepository.findGenresAndCountByUserSpotifyId(spotifyUserId);

        long totalCount = genreData.stream()
                .mapToLong(data -> (Long) data[1])
                .sum();

        return genreData.stream()
                .map(data -> new GenreDistributionDto(
                        (String) data[0],
                        ((Long) data[1]).intValue(),
                        ((Long) data[1]).doubleValue() / totalCount * 100
                ))
                .sorted((a, b) -> Double.compare(b.percentage(), a.percentage()))
                .collect(Collectors.toList());
    }

}
