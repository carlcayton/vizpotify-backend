package com.arian.vizpotifybackend.repository;

import com.arian.vizpotifybackend.model.UserDetail;
import com.arian.vizpotifybackend.model.UserTopArtist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTopArtistRepository extends JpaRepository<UserTopArtist, Long> {
    Optional<UserTopArtist> findFirstByUserDetailSpotifyIdAndPeriodOrderByRankAsc(String spotifyId, UserTopArtist.ListeningPeriod period);

    boolean existsByUserDetailSpotifyId(String spotifyId);
}
