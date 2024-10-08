package com.arian.vizpotifybackend.analytics.era;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface UserMusicEraSummaryRepository extends JpaRepository<UserMusicEraSummary, Long> {
    UserMusicEraSummary findByUserSpotifyIdAndTimeRange(String userSpotifyId, String timeRange);

    UserMusicEraSummary findByUserSpotifyId(String userSpotifyId);

    boolean existsByUserSpotifyId(String userSpotifyId);




}
