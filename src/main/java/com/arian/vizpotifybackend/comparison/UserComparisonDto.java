package com.arian.vizpotifybackend.comparison;

import com.arian.vizpotifybackend.analytics.artist.UserArtistTrackCountMapDto;
import com.arian.vizpotifybackend.analytics.era.UserMusicEraSummaryMapDto;
import com.arian.vizpotifybackend.analytics.features.UserTrackFeatureStatsMapDto;
import com.arian.vizpotifybackend.analytics.genre.UserGenreDistributionMapDto;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class UserComparisonDto {
    private Map<String, UserMusicEraSummaryMapDto> musicEraSummaries;
    private Map<String, UserTrackFeatureStatsMapDto> trackFeatureStats;
    private Map<String, UserGenreDistributionMapDto> genreDistributions;
    private Map<String, UserArtistTrackCountMapDto> artistTrackCounts;
}
