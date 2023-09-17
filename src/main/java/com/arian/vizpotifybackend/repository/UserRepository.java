package com.arian.vizpotifybackend.repository;

import com.arian.vizpotifybackend.dto.ProfileHeaderDTO;
import com.arian.vizpotifybackend.model.UserDetail;
import com.arian.vizpotifybackend.projections.UserFollowersCountProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserDetail,String> {
    @Query("SELECT u FROM UserDetail u WHERE u.spotifyId = :spotifyId")
    Optional<UserDetail> findBySpotifyId(@Param("spotifyId") String spotifyId);

    @Query("SELECT u.followersTotal AS followersCount FROM UserDetail u WHERE u.spotifyId = :spotifyId")
    Optional<UserFollowersCountProjection> findFollowersCountBySpotifyId(@Param("spotifyId") String spotifyId);


}
