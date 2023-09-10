package com.arian.vizpotifybackend.Repository;

import com.arian.vizpotifybackend.model.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserDetail,Long> {
    @Query("SELECT u FROM UserDetail u WHERE u.spotifyId = :spotifyId")
    Optional<UserDetail> findBySpotifyId(@Param("spotifyId") String spotifyId);
}
