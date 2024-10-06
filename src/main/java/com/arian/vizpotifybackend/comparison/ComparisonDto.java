
package com.arian.vizpotifybackend.comparison;

import com.arian.vizpotifybackend.track.AudioFeatureDto;
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

//        @JsonProperty("musicEraSummary")
//        Map<String, Map<String, List<MusicEraSummaryDto>>> musicEraSummary,

        @JsonProperty("audioFeature")
        Map<String, Map<String, List<AudioFeatureDto>>> audioFeature



) {}
