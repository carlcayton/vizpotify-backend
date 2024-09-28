package com.arian.vizpotifybackend.comparison;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record CommonItemsDto(
        @JsonProperty("commonArtists") Map<String, String> commonArtists,
        @JsonProperty("commonTracks") Map<String, Map<String, String>> commonTracks
) {}