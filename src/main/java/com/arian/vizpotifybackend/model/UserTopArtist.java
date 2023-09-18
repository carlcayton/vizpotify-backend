package com.arian.vizpotifybackend.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "user_top_artist")
public class UserTopArtist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_spotify_id")
    private UserDetail userDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id")
    private ArtistDetail artist;

    @Enumerated(EnumType.STRING)
    @Column(name = "period", nullable = false)
    private ListeningPeriod period;

    @Column(name = "rank", nullable = false)
    private int rank;

    @Column(name = "last_updated", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;

    // getters, setters, etc.

    public enum ListeningPeriod {
        SHORT_TERM, MEDIUM_TERM, LONG_TERM
    }
}
