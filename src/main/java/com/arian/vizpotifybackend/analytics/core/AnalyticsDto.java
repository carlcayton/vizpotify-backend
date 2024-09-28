package com.arian.vizpotifybackend.analytics.core;

import com.arian.vizpotifybackend.analytics.features.UserTrackFeatureStatsDto;
import com.arian.vizpotifybackend.analytics.genre.GenreDistributionDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.arian.vizpotifybackend.analytics.artist.ArtistTrackCountDto;
import com.arian.vizpotifybackend.analytics.era.MusicEraSummaryDto;

import java.util.List;
import java.util.Map;

public record AnalyticsDto(
        @JsonProperty("userTrackFeatureStats")
        @JsonPropertyDescription("Map of user track feature statistics grouped by time range. The key represents the time range (e.g., 'shortTerm', 'mediumTerm', 'longTerm').")
        Map<String, UserTrackFeatureStatsDto> userTrackFeatureStats

//        @JsonProperty("userGenreDistribution")
//        @JsonPropertyDescription("List of user's genre distributions grouped by time range. The key represents the time range (e.g., 'shortTerm', 'mediumTerm', 'longTerm').")
//        Map<String, List<GenreDistributionDto>> userGenreDistribution,
//
//        @JsonProperty("userMusicEraSummary")
//        @JsonPropertyDescription("List of user's music era summaries grouped by time range. The key represents the time range (e.g., 'shortTerm', 'mediumTerm', 'longTerm').")
//        Map<String, List<MusicEraSummaryDto>> userMusicEraSummary

//        @JsonProperty("userArtistTrackCount")
//        @JsonPropertyDescription("List of user's artist track counts grouped by time range. The key represents the time range (e.g., 'shortTerm', 'mediumTerm', 'longTerm').")
//        Map<String, List<ArtistTrackCountDto>> userArtistTrackCount
) {
}