package com.arian.vizpotifybackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDTO {
    @JsonProperty("audio_features")
    private Map<String, List<AudioFeatureDTO>> audioFeatures;

    @JsonProperty("genre_distribution")
    private Map<String, List<GenreDistributionDTO>> genreDistribution;

    @JsonProperty("music_era_summary")
    private Map<String, List<MusicEraSummaryDTO>> musicEraSummary;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class AudioFeatureDTO {
//    @JsonProperty("user_spotify_id")
//    private String userSpotifyId;
    private Double acousticness;
    private Double danceability;
    private Double energy;
    private Double instrumentalness;
    private Double liveness;
    private Double speechiness;
    private Double valence;
//    private Double tempo;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class GenreDistributionDTO {
    private String genre;
    @JsonProperty("genre_count")
    private Integer genreCount;
    private Double percentage;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class MusicEraSummaryDTO {
    @JsonProperty("release_date_range")
    private String releaseDateRange;
    @JsonProperty("track_count")
    private Integer trackCount;
    private Double percentage;
}
