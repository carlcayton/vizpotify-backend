package com.arian.vizpotifybackend.model.analytics;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "user_track_feature_stats")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTrackFeatureStats {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_track_feature_stats_id_seq")
    @SequenceGenerator(name = "user_track_feature_stats_id_seq", sequenceName = "user_track_feature_stats_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_spotify_id", nullable = false, length = 255)
    private String userSpotifyId;

    @Column(name = "time_range", nullable = false, length = 50)
    private String timeRange;

    @Column(name = "acousticness", nullable = true, precision = 5, scale = 4)
    private BigDecimal acousticness;

    @Column(name = "danceability", nullable = true, precision = 5, scale = 4)
    private BigDecimal danceability;

    @Column(name = "energy", nullable = true, precision = 5, scale = 4)
    private BigDecimal energy;

    @Column(name = "instrumentalness", nullable = true, precision = 5, scale = 4)
    private BigDecimal instrumentalness;

    @Column(name = "liveness", nullable = true, precision = 5, scale = 4)
    private BigDecimal liveness;

    @Column(name = "speechiness", nullable = true, precision = 5, scale = 4)
    private BigDecimal speechiness;

    @Column(name = "valence", nullable = true, precision = 5, scale = 4)
    private BigDecimal valence;

    @Column(name = "tempo", nullable = true)
    private BigDecimal tempo;
}