package com.arian.vizpotifybackend.projections;

public interface ProfileHeaderProjection {
    String getSpotifyId();
    Integer getFollowedArtistCount();
    Integer getFollowerCount();
    Integer getPlaylistCount();
    String getProfilePictureUrl();
    String getUserDisplayName();
}
