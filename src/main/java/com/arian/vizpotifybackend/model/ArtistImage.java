package com.arian.vizpotifybackend.model;


import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "artist_images")
@Data
public class ArtistImage {

    @Id
    @ManyToOne
    @JoinColumn(name = "artist_id")
    private ArtistDetail artist;

    private String url;

    private Integer height;

    private Integer width;

    // getters, setters, and other methods
}
