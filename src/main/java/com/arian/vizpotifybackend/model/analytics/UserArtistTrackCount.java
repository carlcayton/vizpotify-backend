package com.arian.vizpotifybackend.model.analytics;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_artist_track_count")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserArtistTrackCount {

    @Id
    @SequenceGenerator(name = "user_artist_track_count_id_seq", sequenceName = "user_artist_track_count_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_artist_track_count_id_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_spotify_id", nullable = false, length = 255)
    private String userSpotifyId;

    @Column(name = "time_range", nullable = false, length = 255)
    private String timeRange;

    @Column(name = "artist_name", nullable = false, length = 255)
    private String artistName;

    @Column(name = "track_count", nullable = false)
    private Integer trackCount;
}
