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
    private String artists; // Assuming this is a CSV string of artist names

    private int duration; // Track duration in milliseconds

    private String albumName;

    private String albumImageUrl;

    private int popularity; // Track popularity score

    @Column(name = "release_date")
    private Date releaseDate; // The release date of the track
}

