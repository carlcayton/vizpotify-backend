package com.arian.vizpotifybackend.analytics.era;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserMusicEraSummaryDto(
        String releaseDateRange,
        Integer trackCount,
        Double percentage
) {
}
