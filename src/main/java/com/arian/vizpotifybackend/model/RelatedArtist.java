package com.arian.vizpotifybackend.model;

import com.arian.vizpotifybackend.model.composite.RelatedArtistId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "related_artist")
@IdClass(RelatedArtistId.class)
public class RelatedArtist {

    @Id
    @Column(name = "primary_artist_id")
    private String primaryArtistId;

    @Id
    @Column(name = "related_artist_id")
    private String relatedArtistId;

    @ManyToOne
    @JoinColumn(name = "primary_artist_id", insertable = false, updatable = false)
    private ArtistDetail primaryArtist;

    @ManyToOne
    @JoinColumn(name = "related_artist_id", insertable = false, updatable = false)
    private ArtistDetail relatedArtist;

    // ... getters and setters ...
}