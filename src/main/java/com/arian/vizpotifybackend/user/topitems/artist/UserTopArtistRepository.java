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

    @Query("SELECT ag.genre, COUNT(ag.genre) FROM UserTopArtist uta " +
           "JOIN ArtistDetail ad ON uta.artistId = ad.id " +
           "JOIN ad.genres ag " +
           "WHERE uta.userSpotifyId = :userSpotifyId " +
           "GROUP BY ag.genre")
    List<Object[]> findGenresAndCountByUserSpotifyId(@Param("userSpotifyId") String userSpotifyId);

}
