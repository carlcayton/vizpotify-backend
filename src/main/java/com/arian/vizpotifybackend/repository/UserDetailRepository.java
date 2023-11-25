package com.arian.vizpotifybackend.repository;

import com.arian.vizpotifybackend.model.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserDetailRepository extends JpaRepository<UserDetail,String> {
    @Query("SELECT u FROM UserDetail u WHERE u.spotifyId = :spotifyId")
    Optional<UserDetail> findBySpotifyId(@Param("spotifyId") String spotifyId);

    @Query("SELECT u.followersTotal AS followersCount FROM UserDetail u WHERE u.spotifyId = :spotifyId")
    Integer findFollowersCountBySpotifyId(@Param("spotifyId") String spotifyId);


}
