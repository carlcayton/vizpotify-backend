package com.arian.vizpotifybackend.repository.analytics;

import com.arian.vizpotifybackend.model.analytics.UserMusicEraSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMusicEraSummaryRepository extends JpaRepository<UserMusicEraSummary, Long> {
    UserMusicEraSummary findByUserSpotifyIdAndTimeRange(String userSpotifyId, String timeRange);

    UserMusicEraSummary findByUserSpotifyId(String userSpotifyId);

    boolean existsByUserSpotifyId(String userSpotifyId);
}
