package com.arian.vizpotifybackend.dto.analytics;

public record AudioFeatureDTO(
//  @JsonProperty("user_spotify_id") String userSpotifyId,
        Double acousticness,
        Double danceability,
        Double energy,
        Double instrumentalness,
        Double liveness,
        Double speechiness,
        Double valence
//  Double tempo
) {
}
