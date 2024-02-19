package com.arian.vizpotifybackend.model;

import com.arian.vizpotifybackend.enums.TimeRange;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "user_top_artist")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTopArtist {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "user_spotify_id")
    private String userSpotifyId;

    @Column
    private String artistId;

    @Column(name = "time_range", nullable = false)
    private String timeRange;

    @Column(name = "rank", nullable = false)
    private int rank;

    @Column(name = "last_updated", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;
}
