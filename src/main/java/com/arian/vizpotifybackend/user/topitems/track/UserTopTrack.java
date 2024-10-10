package com.arian.vizpotifybackend.user.topitems.track;

import com.arian.vizpotifybackend.track.TrackDetail;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_id")
    private TrackDetail trackDetail;

    @Column(name = "time_range", nullable = false)
    private String timeRange;

    @Column(name = "rank", nullable = false)
    private int rank;

    @Column(name = "last_updated", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;

}
