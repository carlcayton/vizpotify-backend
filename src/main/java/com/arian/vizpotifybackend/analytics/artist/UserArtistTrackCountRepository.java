package com.arian.vizpotifybackend.analytics.artist;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserArtistTrackCountRepository extends JpaRepository<UserArtistTrackCount, Long> {
    List<UserArtistTrackCount> findAllByUserSpotifyId(String userSpotifyId);
    Optional<UserArtistTrackCount> findFirstByUserSpotifyIdAndTimeRangeOrderByUpdatedAtDesc(String userSpotifyId, String timeRange);
    void deleteByUserSpotifyIdAndTimeRange(String userSpotifyId, String timeRange);
    boolean existsByUserSpotifyId(String userSpotifyId);

    @Modifying
    @Transactional
    @Query(value = """
            WITH ArtistTrackCount AS (
                SELECT 
                    utt.time_range,
                    UNNEST(string_to_array(td.artists, ',')) AS artist_name,
                    COUNT(*) AS track_count
                FROM 
                    user_top_track utt
                JOIN 
                    track_detail td ON utt.track_id = td.id
                WHERE 
                    utt.user_spotify_id = :spotifyUserId
                GROUP BY 
                    utt.time_range, artist_name
            )
            INSERT INTO user_artist_track_count (
                user_spotify_id,
                time_range,
                artist_name,
                track_count,
                percentage,
                created_at,
                updated_at
            )
            SELECT 
                :spotifyUserId AS user_spotify_id,
                time_range,
                artist_name,
                track_count,
                (track_count * 100.0 / SUM(track_count) OVER (PARTITION BY time_range)) AS percentage,
                CURRENT_TIMESTAMP AS created_at,
                CURRENT_TIMESTAMP AS updated_at
            FROM 
                ArtistTrackCount
            """, nativeQuery = true)
    void aggregateAndInsertUserArtistTrackCount(@Param("spotifyUserId") String spotifyUserId);
}
