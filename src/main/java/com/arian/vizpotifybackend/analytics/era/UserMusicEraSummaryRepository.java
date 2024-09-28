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

    @Modifying
    @Transactional
    @Query(value = """
            WITH era_count AS (
                SELECT 
                    utt.user_spotify_id,
                    utt.time_range,
                    CASE
                        WHEN td.release_date >= '2020-01-01' THEN '2020s'
                        WHEN td.release_date BETWEEN '2010-01-01' AND '2019-12-31' THEN '2010s'
                        WHEN td.release_date BETWEEN '2000-01-01' AND '2009-12-31' THEN '2000s'
                        WHEN td.release_date BETWEEN '1990-01-01' AND '1999-12-31' THEN '1990s'
                        WHEN td.release_date BETWEEN '1980-01-01' AND '1989-12-31' THEN '1980s'
                        WHEN td.release_date BETWEEN '1970-01-01' AND '1979-12-31' THEN '1970s'
                        WHEN td.release_date BETWEEN '1960-01-01' AND '1969-12-31' THEN '1960s'
                        WHEN td.release_date BETWEEN '1950-01-01' AND '1959-12-31' THEN '1950s'
                        ELSE '<1950s'
                    END as release_date_range,
                    COUNT(*) as track_count
                FROM 
                    user_top_track utt
                JOIN 
                    track_detail td ON utt.track_id = td.id
                WHERE 
                    utt.user_spotify_id = :spotifyUserId
                GROUP BY 
                    utt.user_spotify_id, utt.time_range, release_date_range
            ),
            total_count AS (
                SELECT 
                    user_spotify_id, 
                    COUNT(*) as total_count 
                FROM 
                    user_top_track 
                WHERE 
                    user_spotify_id = :spotifyUserId AND time_range = 'long_term'
                GROUP BY 
                    user_spotify_id
            )
            INSERT INTO user_music_era_summary (user_spotify_id, time_range, release_date_range, track_count, percentage)
            SELECT 
                ec.user_spotify_id, 
                ec.time_range, 
                ec.release_date_range, 
                ec.track_count, 
                (ec.track_count * 100.0) / tc.total_count as percentage
            FROM 
                era_count ec
            JOIN 
                total_count tc ON ec.user_spotify_id = tc.user_spotify_id
            """, nativeQuery = true)
    void aggregateAndInsertUserMusicEraSummary(@Param("spotifyUserId") String spotifyUserId);



}
