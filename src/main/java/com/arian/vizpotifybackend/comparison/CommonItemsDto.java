package com.arian.vizpotifybackend.comparison;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record CommonItemsDto(
        Map<String, String> commonArtists,
        Map<String, String> commonTracks
) {}
