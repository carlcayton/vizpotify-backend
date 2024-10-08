package com.arian.vizpotifybackend.analytics.era;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMusicEraSummaryRepository extends JpaRepository<UserMusicEraSummary, Long> {
    List<UserMusicEraSummary> findAllByUserSpotifyId(String userSpotifyId);
    UserMusicEraSummary findByUserSpotifyIdAndTimeRange(String userSpotifyId, String timeRange);
    boolean existsByUserSpotifyId(String userSpotifyId);
}
