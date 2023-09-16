package com.arian.vizpotifybackend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileHeaderDTO {

    private Integer followedArtistCount;
    private Integer followerCount;
    private Integer playlistCount;

    private String profilePictureUrl;
    private String spotifyId;
//    private String userDisplayName;
}
