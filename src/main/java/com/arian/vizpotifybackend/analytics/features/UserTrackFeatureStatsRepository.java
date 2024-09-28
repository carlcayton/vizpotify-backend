package com.arian.vizpotifybackend.analytics.features;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTrackFeatureStatsRepository extends JpaRepository<UserTrackFeatureStats, Long> {

    UserTrackFeatureStats findByUserSpotifyIdAndTimeRange(String userSpotifyId, String timeRange);
    List<UserTrackFeatureStats> findAllByUserSpotifyId(String userSpotifyId);
    boolean existsByUserSpotifyId(String userSpotifyId);

    @Modifying
    @Query(value = """
            WITH AggregatedAudioFeatures AS (
            SELECT 
                user_top_track.time_range,
                AVG(audio_feature.acousticness) as acousticness,
                AVG(audio_feature.danceability) as danceability,
                AVG(audio_feature.energy) as energy,
                AVG(audio_feature.instrumentalness) as instrumentalness,
                AVG(audio_feature.liveness) as liveness,
                AVG(audio_feature.speechiness) as speechiness,
                AVG(audio_feature.valence) as valence,
                AVG(audio_feature.tempo) as tempo
            FROM 
                user_detail
            JOIN 
                user_top_track ON user_detail.spotify_id = user_top_track.user_spotify_id
            JOIN 
                audio_feature ON user_top_track.track_id = audio_feature.id
            WHERE 
                user_detail.spotify_id = :spotifyUserId
            GROUP BY 
                user_top_track.time_range
            )
            INSERT INTO user_track_feature_stats (
                user_spotify_id, 
                time_range, 
                acousticness, 
                danceability, 
                energy, 
                instrumentalness, 
                liveness, 
                speechiness, 
                valence, 
                tempo
            )
            SELECT 
                :spotifyUserId,
                time_range,
                acousticness,
                danceability,
                energy,
                instrumentalness,
                liveness,
                speechiness,
                valence,
                tempo
            FROM 
                AggregatedAudioFeatures
            """, nativeQuery = true)
    void aggregateAndInsertUserTrackFeatureStats(@Param("spotifyUserId") String spotifyUserId);

}
