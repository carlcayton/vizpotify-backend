package com.arian.vizpotifybackend.analytics.era;

import com.arian.vizpotifybackend.user.topitems.track.UserTopTrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    public Map<String, Map<String, UserMusicEraSummaryDto>> fetchUserMusicEraSummary(String spotifyUserId) {
        List<UserMusicEraSummary> summaries = userMusicEraSummaryRepository.findAllByUserSpotifyId(spotifyUserId);
        return summaries.stream()
                .collect(Collectors.groupingBy(
                        UserMusicEraSummary::getTimeRange,
                        Collectors.toMap(
                                UserMusicEraSummary::getReleaseDateRange,
                                summary -> new UserMusicEraSummaryDto(
                                        summary.getReleaseDateRange(),
                                        summary.getTrackCount(),
                                        summary.getPercentage()
                                )
                        )
                ));
    }

    // ... (rest of the methods remain the same)
}
