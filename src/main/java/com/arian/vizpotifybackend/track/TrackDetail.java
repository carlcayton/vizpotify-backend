package com.arian.vizpotifybackend.track;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

