package com.arian.vizpotifybackend.user.topitems.track;

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
    @Query(value = """
            SELECT DISTINCT a.track_id 
            FROM user_top_track a 
            JOIN user_top_track b ON a.track_id = b.track_id 
            WHERE a.user_spotify_id = :userIdA AND b.user_spotify_id = :userIdB
            """, nativeQuery = true)
    List<String> fetchCommonTopTracks(@Param("userIdA") String userIdA, @Param("userIdB") String userIdB);

}
