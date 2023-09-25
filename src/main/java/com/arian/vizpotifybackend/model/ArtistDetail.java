package com.arian.vizpotifybackend.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "artist_detail")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtistDetail {

    @Id
    private String id;

    @Column(name = "followers_total")
    private Integer followersTotal;

    @Column(name = "name")
    private String name;

    @Column(name = "popularity")
    private Integer popularity;

    @Column(name = "external_url")
    private String externalUrl;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToMany
    @JoinTable(
            name = "artist_genre",
            joinColumns = @JoinColumn(name = "artist_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres;

}
