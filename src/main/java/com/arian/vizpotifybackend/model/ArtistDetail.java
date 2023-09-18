package com.arian.vizpotifybackend.model;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "artist")
@Data
public class ArtistDetail {

    @Id
    private String id;

    @Column(name = "spotify_url")
    private String spotifyUrl;

    @Column(name = "followers_href")
    private String followersHref;

    @Column(name = "followers_total")
    private Integer followersTotal;

    @Column(name = "spotify_href")
    private String spotifyHref;

    private String name;

    private Integer popularity;

    private String type;

    private String uri;

    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL)
    private List<ArtistImage> images;

    @ManyToMany
    @JoinTable(
            name = "artist_genre",
            joinColumns = @JoinColumn(name = "artist_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres;

}
