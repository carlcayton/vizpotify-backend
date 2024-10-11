package com.arian.vizpotifybackend.comparison;

import com.arian.vizpotifybackend.analytics.artist.UserArtistTrackCountMapDto;
import com.arian.vizpotifybackend.analytics.era.UserMusicEraSummaryMapDto;
import com.arian.vizpotifybackend.analytics.features.UserTrackFeatureStatsMapDto;
import com.arian.vizpotifybackend.analytics.genre.UserGenreDistributionMapDto;
import com.arian.vizpotifybackend.track.TrackDto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public record ComparisonDto(
        @JsonProperty("commonItems")
        CommonItemsDto commonItems,

        @JsonProperty("jaccardSimilarity")
        Map<String, Double> jaccardSimilarity,

        @JsonProperty("tracks")
        Map<String, Map<String, List<TrackDto>>> tracks,

        @JsonProperty("musicEraSummary")
        Map<String, UserMusicEraSummaryMapDto> musicEraSummary,

        @JsonProperty("trackFeatureStats")
        Map<String, UserTrackFeatureStatsMapDto> trackFeatureStats,

        @JsonProperty("genreDistribution")
        Map<String, UserGenreDistributionMapDto> genreDistribution,

        @JsonProperty("artistTrackCount")
        Map<String, UserArtistTrackCountMapDto> artistTrackCount
) {}
