package com.arian.vizpotifybackend.model.analytics;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "user_genre_distribution")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGenreDistribution {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_genre_distribution_id_seq")
    @SequenceGenerator(name = "user_genre_distribution_id_seq", sequenceName = "user_genre_distribution_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_spotify_id", nullable = false, length = 255)
    private String userSpotifyId;

    @Column(name = "time_range", nullable = false, length = 50)
    private String timeRange;

    @Column(name = "genre", nullable = false, length = 100)
    private String genre;

    @Column(name = "genre_count", nullable = false)
    private Integer genreCount;

    @Column(name = "percentage", nullable = false, precision = 5)
    private Double percentage;
}
