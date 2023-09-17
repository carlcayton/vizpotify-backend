package com.arian.vizpotifybackend.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
public class ProfileHeaderDTO {

        private String spotifyId;
        private String userDisplayName;
        private String profilePictureUrl;
        private Integer followedArtistCount;
        private Integer followerCount;
        private Integer playlistCount;
}
