package com.arian.vizpotifybackend.repository.analytics;

import com.arian.vizpotifybackend.model.analytics.UserTrackFeatureStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTrackFeatureStatsRepository extends JpaRepository<UserTrackFeatureStats, Long> {
    UserTrackFeatureStats findByUserSpotifyIdAndTimeRange(String userSpotifyId, String timeRange);
    UserTrackFeatureStats findByUserSpotifyId(String userSpotifyId);
    boolean existsByUserSpotifyId(String userSpotifyId);
}
