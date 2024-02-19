package com.arian.vizpotifybackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "track_detail")
public class TrackDetail {

    @Id
    private String id;

    private String name;

    @Column(name = "artists")
    private String artists;

    private int duration;

    private String albumName;

    private String albumImageUrl;

    private int popularity;

    @Column(name = "release_date")
    private Date releaseDate;
}

