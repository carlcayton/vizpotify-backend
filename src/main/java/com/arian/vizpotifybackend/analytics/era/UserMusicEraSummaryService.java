package com.arian.vizpotifybackend.analytics.era;


import com.arian.vizpotifybackend.user.topitems.track.UserTopTrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserMusicEraSummaryService {
    private final UserTopTrackRepository userTopTrackRepository;
    private final UserMusicEraSummaryRepository userMusicEraSummaryRepository;

    @Transactional
    public void aggregateAndUpsertMusicEraSummary(String spotifyUserId) {
        List<UserTrackEraDataProjection> userTrackEraData = userTopTrackRepository.findUserTrackEraData(spotifyUserId);
        Map<String, List<UserTrackEraDataProjection>> groupedByTimeRange = groupByTimeRange(userTrackEraData);
        Map<String, Map<String, Integer>> eraCounts = calculateEraCounts(groupedByTimeRange);
        long totalCount = calculateTotalCount(groupedByTimeRange);
        List<UserMusicEraSummary> summaries = createSummaries(spotifyUserId, eraCounts, totalCount);
        userMusicEraSummaryRepository.saveAll(summaries);
    }

    private Map<String, List<UserTrackEraDataProjection>> groupByTimeRange(List<UserTrackEraDataProjection> trackEraData) {
        return trackEraData.stream()
                .collect(Collectors.groupingBy(UserTrackEraDataProjection::getTimeRange));
    }

    private Map<String, Map<String, Integer>> calculateEraCounts(Map<String, List<UserTrackEraDataProjection>> groupedTracks) {
        Map<String, Map<String, Integer>> eraCounts = new HashMap<>();
        for (Map.Entry<String, List<UserTrackEraDataProjection>> entry : groupedTracks.entrySet()) {
            Map<String, Integer> countsByEra = entry.getValue().stream()
                    .collect(Collectors.groupingBy(
                            track -> getEraFromDate(track.getReleaseDate()),
                            Collectors.summingInt(e -> 1)
                    ));
            eraCounts.put(entry.getKey(), countsByEra);
        }
        return eraCounts;
    }

    private long calculateTotalCount(Map<String, List<UserTrackEraDataProjection>> groupedTracks) {
        return groupedTracks.getOrDefault("long_term", Collections.emptyList()).size();
    }

    private List<UserMusicEraSummary> createSummaries(String spotifyUserId,
                                                      Map<String, Map<String, Integer>> eraCounts,
                                                      long totalCount) {
        List<UserMusicEraSummary> summaries = new ArrayList<>();
        for (Map.Entry<String, Map<String, Integer>> timeRangeEntry : eraCounts.entrySet()) {
            for (Map.Entry<String, Integer> eraEntry : timeRangeEntry.getValue().entrySet()) {
                UserMusicEraSummary summary = UserMusicEraSummary.builder()
                        .userSpotifyId(spotifyUserId)
                        .timeRange(timeRangeEntry.getKey())
                        .releaseDateRange(eraEntry.getKey())
                        .trackCount(eraEntry.getValue())
                        .percentage((eraEntry.getValue() * 100.0) / totalCount)
                        .build();
                summaries.add(summary);
            }
        }
        return summaries;
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
