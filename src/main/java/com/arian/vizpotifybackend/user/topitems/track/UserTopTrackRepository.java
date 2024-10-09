package com.arian.vizpotifybackend.user.topitems.track;

import com.arian.vizpotifybackend.analytics.era.UserTrackEraDataProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTopTrackRepository extends JpaRepository<UserTopTrack, Long> {
    List<UserTopTrack> findByUserSpotifyId(String spotifyId);

    boolean existsByUserSpotifyId(String spotifyId);

    List<UserTopTrack> findByUserSpotifyIdAndTimeRange(String userSpotifyId, String timeRange);

    @Query("SELECT utt FROM UserTopTrack utt JOIN FETCH utt.trackDetail WHERE utt.userSpotifyId = :userSpotifyId AND utt.timeRange = :timeRange")
    List<UserTopTrack> findByUserSpotifyIdAndTimeRangeWithTrackDetails(@Param("userSpotifyId") String userSpotifyId, @Param("timeRange") String timeRange);
    @Query(value = """
            SELECT DISTINCT a.track_id 
            FROM user_top_track a 
            JOIN user_top_track b ON a.track_id = b.track_id 
            WHERE a.user_spotify_id = :userIdA AND b.user_spotify_id = :userIdB
            """, nativeQuery = true)
    List<String> fetchCommonTopTracks(@Param("userIdA") String userIdA, @Param("userIdB") String userIdB);

    @Query(value = "SELECT track_id FROM user_top_track WHERE user_spotify_id = :userSpotifyId AND time_range = :timeRange", nativeQuery = true)
    List<String> findTrackIdsByUserSpotifyIdAndTimeRange(@Param("userSpotifyId") String userSpotifyId, @Param("timeRange") String timeRange);

    @Query("SELECT utt.timeRange as timeRange, td.releaseDate as releaseDate " +
            "FROM UserTopTrack utt JOIN utt.trackDetail td " +
            "WHERE utt.userSpotifyId = :spotifyUserId")
    List<UserTrackEraDataProjection> findUserTrackEraData(@Param("spotifyUserId") String spotifyUserId);


}

