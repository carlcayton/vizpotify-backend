package com.arian.vizpotifybackend.dto.comparison;


import com.arian.vizpotifybackend.dto.TrackDTO;
import com.arian.vizpotifybackend.dto.analytics.AudioFeatureDTO;
import com.arian.vizpotifybackend.dto.analytics.MusicEraSummaryDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ComparisonDTO {
    @JsonProperty("common_items")
    private CommonItemsDTO commonItems;

    @JsonProperty("jaccard_similarity")
    private Map<String, Double> jaccardSimilarity;
    @JsonProperty("tracks")
    private Map<String, Map<String, TrackDTO>> oldAndNewTrackDTO;
    @JsonProperty("music_era_summary")
    private Map<String, Map<String, List<MusicEraSummaryDTO>>> musicEraSummary;
    @JsonProperty("audio_feature")
    private Map<String, Map<String, List<AudioFeatureDTO>>> audioFeature;
}

@Data
class CommonItemsDTO {
    @JsonProperty("common_artists")
    private Map<String, String> commonArtists;
    @JsonProperty("common_tracks")
    private Map<String, Map<String,String>> commonTracks;
}
