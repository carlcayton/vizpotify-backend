package com.arian.vizpotifybackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class UserHeaderStat {



    @Id
    private String userSpotifyId;
    private Integer followedArtistCount;
    private Integer followerCount;
    private Integer playlistCount;
}
