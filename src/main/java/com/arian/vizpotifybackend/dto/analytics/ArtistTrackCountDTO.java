package com.arian.vizpotifybackend.dto.analytics;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ArtistTrackCountDTO(
        @JsonProperty("artist_name") String artistName,
        @JsonProperty("track_count") Integer trackCount
) {
}
