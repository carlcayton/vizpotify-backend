package com.arian.vizpotifybackend.comparison;

import com.arian.vizpotifybackend.analytics.artist.UserArtistTrackCountMapDto;
import com.arian.vizpotifybackend.analytics.artist.UserArtistTrackCountService;
import com.arian.vizpotifybackend.analytics.era.UserMusicEraSummaryMapDto;
import com.arian.vizpotifybackend.analytics.era.UserMusicEraSummaryService;
import com.arian.vizpotifybackend.analytics.features.UserTrackFeatureStatsMapDto;
import com.arian.vizpotifybackend.analytics.features.UserTrackFeatureStatsService;
import com.arian.vizpotifybackend.analytics.genre.UserGenreDistributionMapDto;
import com.arian.vizpotifybackend.analytics.genre.UserGenreDistributionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserComparisonService {

    private final UserMusicEraSummaryService userMusicEraSummaryService;
    private final UserTrackFeatureStatsService userTrackFeatureStatsService;
    private final UserGenreDistributionService userGenreDistributionService;
    private final UserArtistTrackCountService userArtistTrackCountService;

    public UserComparisonDto compareUsers(String userId1, String userId2) {
        Map<String, UserMusicEraSummaryMapDto> musicEraSummaries = new HashMap<>();
        Map<String, UserTrackFeatureStatsMapDto> trackFeatureStats = new HashMap<>();
        Map<String, UserGenreDistributionMapDto> genreDistributions = new HashMap<>();
        Map<String, UserArtistTrackCountMapDto> artistTrackCounts = new HashMap<>();

        musicEraSummaries.put(userId1, userMusicEraSummaryService.fetchUserMusicEraSummary(userId1));
        musicEraSummaries.put(userId2, userMusicEraSummaryService.fetchUserMusicEraSummary(userId2));

        trackFeatureStats.put(userId1, userTrackFeatureStatsService.fetchUserTrackFeatureStats(userId1));
        trackFeatureStats.put(userId2, userTrackFeatureStatsService.fetchUserTrackFeatureStats(userId2));

        genreDistributions.put(userId1, userGenreDistributionService.fetchUserGenreDistribution(userId1));
        genreDistributions.put(userId2, userGenreDistributionService.fetchUserGenreDistribution(userId2));

        artistTrackCounts.put(userId1, userArtistTrackCountService.fetchUserArtistTrackCount(userId1));
        artistTrackCounts.put(userId2, userArtistTrackCountService.fetchUserArtistTrackCount(userId2));

        return UserComparisonDto.builder()
                .musicEraSummaries(musicEraSummaries)
                .trackFeatureStats(trackFeatureStats)
                .genreDistributions(genreDistributions)
                .artistTrackCounts(artistTrackCounts)
                .build();
    }
}
