package com.arian.vizpotifybackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "user_top_track")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTopTrack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_spotify_id")
    private String userSpotifyId;

    @Column(name = "track_id")
    private String trackId;

    @Column(name = "time_range", nullable = false)
    private String timeRange;

    @Column(name = "rank", nullable = false)
    private int rank;

    @Column(name = "last_updated", nullable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;

    // Additional fields specific to tracks can be added here
    // For example, you might want to store track name, duration, or album
}
