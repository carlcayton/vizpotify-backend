package com.arian.vizpotifybackend.analytics.genre;

import com.arian.vizpotifybackend.user.topitems.artist.UserTopArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserGenreDistributionService {
    private final UserTopArtistRepository userTopArtistRepository;
    private final UserGenreDistributionRepository userGenreDistributionRepository;
    private final UserGenreDistributionMapper userGenreDistributionMapper;

    private static final int DAYS_BEFORE_REFRESH = 9;

    @Transactional
    public UserGenreDistributionMapDto fetchUserGenreDistribution(String spotifyId) {
        aggregateAndUpsertUserGenreDistribution(spotifyId);
        List<UserGenreDistribution> userGenreDistributions = userGenreDistributionRepository.findByUserSpotifyIdOrderByPercentageDesc(spotifyId);
        return userGenreDistributionMapper.toMapDto(spotifyId, userGenreDistributions);
    }

    @Transactional
    public void aggregateAndUpsertUserGenreDistribution(String spotifyUserId) {
        Stream.of("short_term", "medium_term", "long_term")
                .forEach(timeRange -> {
                    Optional<UserGenreDistribution> existingDistribution = userGenreDistributionRepository
                            .findFirstByUserSpotifyIdAndTimeRangeOrderByUpdatedAtDesc(spotifyUserId, timeRange);

                    if (existingDistribution.isEmpty() || isDistributionOutdated(existingDistribution.get())) {
                        List<GenreDistributionDto> genreDistributions = calculateGenreDistribution(spotifyUserId, timeRange);
                        saveGenreDistributions(spotifyUserId, timeRange, genreDistributions);
                    }
                });
    }

    private boolean isDistributionOutdated(UserGenreDistribution distribution) {
        return distribution.getUpdatedAt().plusDays(DAYS_BEFORE_REFRESH).isBefore(LocalDateTime.now());
    }

    private List<GenreDistributionDto> calculateGenreDistribution(String spotifyUserId, String timeRange) {
        List<Object[]> genreData = userTopArtistRepository.findGenresAndCountByUserSpotifyIdAndTimeRange(spotifyUserId, timeRange);
        
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

    private void saveGenreDistributions(String spotifyUserId, String timeRange, List<GenreDistributionDto> genreDistributions) {
        userGenreDistributionRepository.deleteByUserSpotifyIdAndTimeRange(spotifyUserId, timeRange);
        
        genreDistributions.forEach(dto -> {
            UserGenreDistribution distribution = UserGenreDistribution.builder()
                    .userSpotifyId(spotifyUserId)
                    .timeRange(timeRange)
                    .genre(dto.genre())
                    .genreCount(dto.genreCount())
                    .percentage(dto.percentage())
                    .build();

            userGenreDistributionRepository.save(distribution);
        });
    }
}
