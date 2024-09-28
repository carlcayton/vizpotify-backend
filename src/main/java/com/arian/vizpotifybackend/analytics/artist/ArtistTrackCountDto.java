package com.arian.vizpotifybackend.analytics.artist;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ArtistTrackCountDto(
        @JsonProperty("artistName") String artistName,
        @JsonProperty("trackCount") Integer trackCount
) {
}
