package com.arian.vizpotifybackend.user.topitems.artist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserTopArtistRepository extends JpaRepository<UserTopArtist, Long> {
    List<UserTopArtist> findByUserSpotifyId(String spotifyId);

    boolean existsByUserSpotifyId(String spotifyId);

    List<UserTopArtist> findByUserSpotifyIdAndTimeRange(String userSpotifyId, String timeRange);

    @Query(value = """
            SELECT DISTINCT a.artist_id 
            FROM user_top_artist a 
            JOIN user_top_artist b ON a.artist_id = b.artist_id 
            WHERE a.user_spotify_id = :userIdA AND b.user_spotify_id = :userIdB
            """, nativeQuery = true)
    List<String> fetchCommonTopArtists(@Param("userIdA") String userIdA, @Param("userIdB") String userIdB);


    @Query(nativeQuery = true, value =
            "SELECT ag.genre, COUNT(ag.genre) as genre_count " +
                    "FROM user_top_artist uta " +
                    "JOIN artist_detail ad ON uta.artist_id = ad.id " +
                    "JOIN artist_genre ag ON ad.id = ag.artist_id " +
                    "WHERE uta.user_spotify_id = :userSpotifyId " +
                    "GROUP BY ag.genre " +
                    "ORDER BY genre_count DESC " +
                    "LIMIT 10")
    List<Object[]> findGenresAndCountByUserSpotifyId(@Param("userSpotifyId") String userSpotifyId);
}
