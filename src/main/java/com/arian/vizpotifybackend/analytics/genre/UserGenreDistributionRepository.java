package com.arian.vizpotifybackend.analytics.genre;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserGenreDistributionRepository extends JpaRepository<UserGenreDistribution, Long> {
    List<UserGenreDistribution> findByUserSpotifyIdAndTimeRange(String userSpotifyId, String timeRange);
    boolean existsByUserSpotifyId(String userSpotifyId);
    List<UserGenreDistribution> findByUserSpotifyIdOrderByPercentageDesc(String userSpotifyId);
}
