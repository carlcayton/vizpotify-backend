package com.arian.vizpotifybackend.analytics.era;

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
public class UserMusicEraSummaryService {
    private final UserTopTrackRepository userTopTrackRepository;
    private final UserMusicEraSummaryRepository userMusicEraSummaryRepository;
    private final UserMusicEraSummaryMapper userMusicEraSummaryMapper;

    @Transactional
    public UserMusicEraSummaryMapDto fetchUserMusicEraSummary(String spotifyId) {
        aggregateAndUpsertMusicEraSummary(spotifyId);
        List<UserMusicEraSummary> userMusicEraSummaries = userMusicEraSummaryRepository.findAllByUserSpotifyId(spotifyId);
        return userMusicEraSummaryMapper.toMapDto(spotifyId, userMusicEraSummaries);
    }

    @Transactional
    public void aggregateAndUpsertMusicEraSummary(String spotifyUserId) {
        Stream.of("short_term", "medium_term", "long_term")
                .forEach(timeRange -> {
                    Optional<UserMusicEraSummary> existingSummary = userMusicEraSummaryRepository
                            .findFirstByUserSpotifyIdAndTimeRangeOrderByUpdatedAtDesc(spotifyUserId, timeRange);

                    if (existingSummary.isEmpty() || isSummaryOutdated(existingSummary.get())) {
                        List<UserTrackEraDataProjection> userTrackEraData = userTopTrackRepository.findUserTrackEraData(spotifyUserId);
                        Map<String, Integer> eraCounts = calculateEraCounts(userTrackEraData);
                        long totalCount = calculateTotalCount(eraCounts);
                        List<UserMusicEraSummary> summaries = createSummaries(spotifyUserId, timeRange, eraCounts, totalCount);
                        userMusicEraSummaryRepository.deleteByUserSpotifyIdAndTimeRange(spotifyUserId, timeRange);
                        userMusicEraSummaryRepository.saveAll(summaries);
                    }
                });
    }

    private boolean isSummaryOutdated(UserMusicEraSummary summary) {
        return summary.getUpdatedAt().plusDays(7).isBefore(LocalDateTime.now());
    }

    private Map<String, Integer> calculateEraCounts(List<UserTrackEraDataProjection> trackEraData) {
        return trackEraData.stream()
                .collect(Collectors.groupingBy(
                        track -> getEraFromDate(track.getReleaseDate()),
                        Collectors.summingInt(e -> 1)
                ));
    }

    private long calculateTotalCount(Map<String, Integer> eraCounts) {
        return eraCounts.values().stream().mapToInt(Integer::intValue).sum();
    }

    private List<UserMusicEraSummary> createSummaries(String spotifyUserId, String timeRange, Map<String, Integer> eraCounts, long totalCount) {
        return eraCounts.entrySet().stream()
                .map(entry -> UserMusicEraSummary.builder()
                        .userSpotifyId(spotifyUserId)
                        .timeRange(timeRange)
                        .releaseDateRange(entry.getKey())
                        .trackCount(entry.getValue())
                        .percentage((entry.getValue() * 100.0) / totalCount)
                        .build())
                .collect(Collectors.toList());
    }

    private String getEraFromDate(Date date) {
        int year = date.getYear() + 1900;
        if (year >= 2020) return "2020s";
        if (year >= 2010) return "2010s";
        if (year >= 2000) return "2000s";
        if (year >= 1990) return "1990s";
        if (year >= 1980) return "1980s";
        if (year >= 1970) return "1970s";
        if (year >= 1960) return "1960s";
        if (year >= 1950) return "1950s";
        return "<1950s";
    }
}
