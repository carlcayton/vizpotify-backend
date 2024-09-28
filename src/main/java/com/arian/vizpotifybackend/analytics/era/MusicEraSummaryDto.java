package com.arian.vizpotifybackend.analytics.era;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MusicEraSummaryDto(
        String releaseDateRange,
        Integer trackCount,
        Double percentage
) {
}
