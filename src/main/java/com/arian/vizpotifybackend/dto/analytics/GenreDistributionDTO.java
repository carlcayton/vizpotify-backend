package com.arian.vizpotifybackend.dto.analytics;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GenreDistributionDTO(
        String genre,
        @JsonProperty("genre_count") Integer genreCount,
        Double percentage
) {
}
