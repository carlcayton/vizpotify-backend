package com.arian.vizpotifybackend.analytics.genre;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserGenreDistributionRepository extends JpaRepository<UserGenreDistribution, Long> {
    UserGenreDistribution findByUserSpotifyIdAndTimeRange(String userSpotifyId, String timeRange);
    UserGenreDistribution findByUserSpotifyId(String userSpotifyId);
    boolean existsByUserSpotifyId(String userSpotifyId);

    @Modifying
    @Transactional
    @Query(value = """
            WITH AggregatedGenres AS (
                SELECT 
                    uta.user_spotify_id, 
                    uta.time_range, 
                    ag.genre AS genre,
                    COUNT(ag.genre) AS genre_count, 
                    COUNT(ag.genre) * 100.0 / SUM(COUNT(ag.genre)) OVER (PARTITION BY uta.time_range) AS percentage
                FROM 
                    user_top_artist uta
                JOIN 
                    artist_genre ag ON uta.artist_id = ag.artist_id
                WHERE 
                    uta.user_spotify_id = :spotifyUserId
                GROUP BY 
                    uta.user_spotify_id, uta.time_range, ag.genre
            )
            INSERT INTO user_genre_distribution (user_spotify_id, time_range, genre, genre_count, percentage)
            SELECT 
                user_spotify_id, time_range, genre, genre_count, percentage
            FROM 
                AggregatedGenres
            """, nativeQuery = true)
    void aggregateAndInsertUserGenreDistribution(@Param("spotifyUserId") String spotifyUserId);


    List<UserGenreDistribution> findByUserSpotifyIdOrderByPercentageDesc(String userSpotifyId);

}
