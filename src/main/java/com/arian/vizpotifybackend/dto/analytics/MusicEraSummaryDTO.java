package com.arian.vizpotifybackend.dto.analytics;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MusicEraSummaryDTO(
        @JsonProperty("release_date_range") String releaseDateRange,
        @JsonProperty("track_count") Integer trackCount,
        Double percentage
) {
}
