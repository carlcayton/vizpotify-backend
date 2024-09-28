package com.arian.vizpotifybackend.analytics.artist;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserArtistTrackCountRepository extends JpaRepository<UserArtistTrackCount, Long> {
    UserArtistTrackCount findByUserSpotifyIdAndTimeRange(String userSpotifyId, String timeRange);

    UserArtistTrackCount findByUserSpotifyId(String userSpotifyId);

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
                track_count
            )
            SELECT 
                :spotifyUserId AS user_spotify_id,
                time_range,
                artist_name,
                SUM(track_count) AS track_count
            FROM 
                ArtistTrackCount
            GROUP BY 
                time_range, artist_name
            """, nativeQuery = true)
    void aggregateAndInsertUserArtistTrackCount(@Param("spotifyUserId") String spotifyUserId);

    @Query(value = """
            SELECT 
                artist_name, 
                track_count, 
                time_range 
            FROM 
                user_artist_track_count 
            WHERE 
                user_spotify_id = :spotifyUserId
            ORDER BY 
                time_range, track_count DESC
            """, nativeQuery = true)
    List<Object[]> fetchUserArtistTrackCount(@Param("spotifyUserId") String spotifyUserId);
}
