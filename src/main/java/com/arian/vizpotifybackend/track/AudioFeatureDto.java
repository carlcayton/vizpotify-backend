package com.arian.vizpotifybackend.track;

public record AudioFeatureDto(
        Double acousticness,
        Double danceability,
        Double energy,
        Double instrumentalness,
        Double liveness,
        Double speechiness,
        Double valence
) {
}
