package com.arian.vizpotifybackend.repository.analytics;

import com.arian.vizpotifybackend.model.analytics.UserGenreDistribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserGenreDistributionRepository extends JpaRepository<UserGenreDistribution, Long> {
    UserGenreDistribution findByUserSpotifyIdAndTimeRange(String userSpotifyId, String timeRange);
    UserGenreDistribution findByUserSpotifyId(String userSpotifyId);
    boolean existsByUserSpotifyId(String userSpotifyId);
}
