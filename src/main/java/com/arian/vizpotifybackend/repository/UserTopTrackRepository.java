package com.arian.vizpotifybackend.repository;

import com.arian.vizpotifybackend.model.UserTopTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserTopTrackRepository extends JpaRepository<UserTopTrack, Long> {
    List<UserTopTrack> findByUserSpotifyId(String spotifyId);

    boolean existsByUserSpotifyId(String spotifyId);

    List<UserTopTrack> findByUserSpotifyIdAndTimeRange(String userSpotifyId, String timeRange);
}
