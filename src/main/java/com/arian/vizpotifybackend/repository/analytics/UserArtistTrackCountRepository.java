package com.arian.vizpotifybackend.repository.analytics;

import com.arian.vizpotifybackend.model.analytics.UserArtistTrackCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserArtistTrackCountRepository extends JpaRepository<UserArtistTrackCount, Long> {
    UserArtistTrackCount findByUserSpotifyIdAndTimeRange(String userSpotifyId, String timeRange);

    UserArtistTrackCount findByUserSpotifyId(String userSpotifyId);

    boolean existsByUserSpotifyId(String userSpotifyId);
}
