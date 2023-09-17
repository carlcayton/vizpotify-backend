package com.arian.vizpotifybackend.repository;

import com.arian.vizpotifybackend.dto.ProfileHeaderDTO;
import com.arian.vizpotifybackend.model.UserHeaderStat;
import com.arian.vizpotifybackend.projections.ProfileHeaderProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserHeaderStatRepository extends JpaRepository<UserHeaderStat, String> {
    Optional<UserHeaderStat> findById(String userSpotifyId);

    @Query("SELECT ud.spotifyId AS spotifyId, uhs.followedArtistCount AS followedArtistCount, uhs.followerCount AS followerCount, uhs.playlistCount AS playlistCount, ud.profilePictureUrl AS profilePictureUrl, ud.displayName AS userDisplayName FROM UserDetail ud, UserHeaderStat uhs WHERE ud.spotifyId = uhs.userSpotifyId AND ud.spotifyId = :spotifyId")
    ProfileHeaderProjection getProfileHeaderBySpotifyId(String spotifyId);

}
