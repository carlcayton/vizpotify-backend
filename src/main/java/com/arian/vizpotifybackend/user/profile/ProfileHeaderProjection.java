package com.arian.vizpotifybackend.user.profile;

public interface ProfileHeaderProjection {
    String getSpotifyId();
    Integer getFollowedArtistCount();
    Integer getFollowerCount();
    Integer getPlaylistCount();
    String getProfilePictureUrl();
    String getUserDisplayName();
}
