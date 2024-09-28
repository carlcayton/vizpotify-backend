package com.arian.vizpotifybackend.user.profile;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ProfileHeaderDto {

        private String spotifyId;
        private String userDisplayName;
        private String profilePictureUrl;
        private Integer followedArtistCount;
        private Integer followerCount;
        private Integer playlistCount;
}
