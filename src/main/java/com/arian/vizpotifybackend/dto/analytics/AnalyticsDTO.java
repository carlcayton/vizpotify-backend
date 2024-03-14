
package com.arian.vizpotifybackend.dto.analytics;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public record AnalyticsDTO(
        @JsonProperty("audio_features") Map<String, List<AudioFeatureDTO>> audioFeatures,
        @JsonProperty("genre_distribution") Map<String, List<GenreDistributionDTO>> genreDistribution,
        @JsonProperty("music_era_summary") Map<String, List<MusicEraSummaryDTO>> musicEraSummary,
        @JsonProperty("artist_track_count") Map<String, List<ArtistTrackCountDTO>> userArtistTrackCount
) {}

