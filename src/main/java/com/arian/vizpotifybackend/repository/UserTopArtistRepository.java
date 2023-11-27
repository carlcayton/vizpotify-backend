package com.arian.vizpotifybackend.repository;

import com.arian.vizpotifybackend.model.UserDetail;
import com.arian.vizpotifybackend.model.UserTopArtist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserTopArtistRepository extends JpaRepository<UserTopArtist, Long> {
    List<UserTopArtist> findByUserSpotifyId(String spotifyId);

    boolean existsByUserSpotifyId(String spotifyId);

    List<UserTopArtist> findByUserSpotifyIdAndTimeRange(String userSpotifyId, String timeRange);

}
