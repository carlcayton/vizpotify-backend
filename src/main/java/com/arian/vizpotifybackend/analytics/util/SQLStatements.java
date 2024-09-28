package com.arian.vizpotifybackend.analytics.util;

import org.springframework.stereotype.Component;

public class SQLStatements {
    public static final String AGGREGATE_AND_INSERT_AUDIO_FEATURES = """
            WITH AggregatedAudioFeatures AS (
            SELECT\s
                user_top_track.time_range,
                AVG(audio_feature.acousticness) as acousticness,
                AVG(audio_feature.danceability) as danceability,
                AVG(audio_feature.energy) as energy,
                AVG(audio_feature.instrumentalness) as instrumentalness,
                AVG(audio_feature.liveness) as liveness,
                AVG(audio_feature.speechiness) as speechiness,
                AVG(audio_feature.valence) as valence,
                AVG(audio_feature.tempo) as tempo
            FROM\s
                user_detail
            JOIN\s
                user_top_track ON user_detail.spotify_id = user_top_track.user_spotify_id
            JOIN\s
                audio_feature ON user_top_track.track_id = audio_feature.id
            WHERE\s
                user_detail.spotify_id = ?
            GROUP BY\s
                user_top_track.time_range
            )
            INSERT INTO user_track_feature_stats (
                user_spotify_id,\s
                time_range,\s
                acousticness,\s
                danceability,\s
                energy,\s
                instrumentalness,\s
                liveness,\s
                speechiness,\s
                valence,\s
                tempo
            )
            SELECT\s
                ?,
                time_range,
                acousticness,
                danceability,
                energy,
                instrumentalness,
                liveness,
                speechiness,
                valence,
                tempo
            FROM\s
                AggregatedAudioFeatures
            """;

    public static final String FETCH_AUDIO_FEATURE_ANALYTICS = """
            SELECT
                time_range,
                acousticness,
                danceability,
                energy,
                instrumentalness,
                liveness,
                speechiness,
                valence
            FROM
                user_track_feature_stats
            WHERE
                user_spotify_id = ?
            """;

    public static final String AGGREGATE_AND_INSERT_USER_GENRE_DISTRIBUTION = """
            WITH AggregatedGenres AS (
                SELECT\s
                    uta.user_spotify_id,\s
                    uta.time_range,\s
                    ag.genre AS genre,
                    COUNT(ag.genre) AS genre_count,\s
                    COUNT(ag.genre) * 100.0 / SUM(COUNT(ag.genre)) OVER (PARTITION BY uta.time_range) AS percentage
                FROM\s
                    user_top_artist uta
                JOIN\s
                    artist_genre ag ON uta.artist_id = ag.artist_id
                WHERE\s
                    uta.user_spotify_id = ?
                GROUP BY\s
                    uta.user_spotify_id, uta.time_range, ag.genre
            )
            INSERT INTO user_genre_distribution (user_spotify_id, time_range, genre, genre_count, percentage)
            SELECT\s
                user_spotify_id, time_range, genre, genre_count, percentage
            FROM\s
                AggregatedGenres
            """;

    public static final String AGGREGATE_MUSIC_ERA_SUMMARY = """
            WITH era_count AS (
                SELECT\s
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
                FROM\s
                    user_top_track utt
                JOIN\s
                    track_detail td ON utt.track_id = td.id
                WHERE\s
                    utt.user_spotify_id = ?
                GROUP BY\s
                    utt.user_spotify_id, utt.time_range, release_date_range
            ),
            total_count AS (
                SELECT\s
                    user_spotify_id,\s
                    COUNT(*) as total_count\s
                FROM\s
                    user_top_track\s
                WHERE\s
                    user_spotify_id = ? AND time_range = 'long_term'
                GROUP BY\s
                    user_spotify_id
            )
            INSERT INTO user_music_era_summary (user_spotify_id, time_range, release_date_range, track_count, percentage)
            SELECT\s
                ec.user_spotify_id,\s
                ec.time_range,\s
                ec.release_date_range,\s
                ec.track_count,\s
                (ec.track_count * 100.0) / tc.total_count as percentage
            FROM\s
                era_count ec
            JOIN\s
                total_count tc ON ec.user_spotify_id = tc.user_spotify_id
            """;

    public static final String AGGREGATE_AND_INSERT_USER_ARTIST_TRACK_COUNT = """
            WITH ArtistTrackCount AS (
                SELECT\s
                    utt.time_range,
                    UNNEST(string_to_array(td.artists, ',')) AS artist_name,
                    COUNT(*) AS track_count
                FROM\s
                    user_top_track utt
                JOIN\s
                    track_detail td ON utt.track_id = td.id
                WHERE\s
                    utt.user_spotify_id = ?
                GROUP BY\s
                    utt.time_range, artist_name
            )
            INSERT INTO user_artist_track_count (
                user_spotify_id,
                time_range,
                artist_name,
                track_count
            )
            SELECT\s
                ? AS user_spotify_id,
                time_range,
                artist_name,
                SUM(track_count) AS track_count
            FROM\s
                ArtistTrackCount
            GROUP BY\s
                time_range, artist_name
            """;

    public static final String FETCH_MUSIC_ERA_SUMMARY = """
            SELECT time_range, release_date_range, track_count, percentage
            FROM user_music_era_summary
            WHERE user_spotify_id = ?
            """;

    public static final String FETCH_USER_GENRE_DISTRIBUTION = """
            WITH RankedGenres AS (
                SELECT
                    time_range,
                    genre,
                    percentage,
                    genre_count,
                    ROW_NUMBER() OVER (PARTITION BY time_range ORDER BY percentage DESC) as rn
                FROM
                    user_genre_distribution
                WHERE
                    user_spotify_id = ?
            )
            SELECT
                time_range,
                genre,
                genre_count,
                percentage
            FROM
                RankedGenres
            WHERE
                rn <= 12
            """;

    public static final String FETCH_USER_ARTIST_TRACK_COUNT = """
            SELECT\s
                artist_name,\s
                track_count,\s
                time_range\s
            FROM\s
                user_artist_track_count\s
            WHERE\s
                user_spotify_id = ?
            ORDER BY\s
                time_range, track_count DESC
            """;

    public static final String FETCH_USER_TOP_ARTIST = """
            SELECT artist_id
            FROM user_top_artist
            WHERE user_spotify_id = ?
            """;

    public static final String FETCH_USER_TOP_TRACK = """
            SELECT track_id
            FROM user_top_track
            WHERE user_spotify_id = ?
            """;

    public static final String FETCH_USER_TOTAL_DISTINCT_ARTISTS = """
            SELECT COUNT(DISTINCT artist_id)
            FROM user_top_artist
            WHERE user_spotify_id = ?
            """;

    public static final String FETCH_USER_TOTAL_DISTINCT_TRACKS = """
            SELECT COUNT(DISTINCT track_id)
            FROM user_top_track
            WHERE user_spotify_id = ?
            """;

    public static final String FETCH_COMMON_TOP_TRACKS = """
            SELECT DISTINCT a.track_id
            FROM user_top_track a
                     JOIN user_top_track b ON a.track_id = b.track_id
            WHERE a.user_spotify_id = ? AND b.user_spotify_id = ?
            """;

    public static final String FETCH_COMMON_TOP_ARTISTS = """
            SELECT DISTINCT a.artist_id
            FROM user_top_artist a
                     JOIN user_top_artist b ON a.artist_id = b.artist_id
            WHERE a.user_spotify_id = ? AND b.user_spotify_id = ?
            """;

    public static final String FETCH_ARTIST_NAMES_BY_IDS = "SELECT id, name FROM artist_detail WHERE id = ANY(?)";

    public static final String FETCH_TRACK_NAMES_BY_IDS = "SELECT id, name, artists FROM track_detail WHERE id = ANY(?)";

    public static final String FETCH_NEWEST_TRACK_FOR_USER = """
            SELECT t.id AS track_id, t.album_image_url, t.name, t.artists, t.release_date
            FROM user_top_track u
            JOIN track_detail t ON u.track_id = t.id
            WHERE u.user_spotify_id = ?
            ORDER BY t.release_date DESC
            LIMIT 1
            """;

    public static final String FETCH_OLDEST_TRACK_FOR_USER = """
            SELECT t.id AS track_id, t.album_image_url, t.name, t.artists, t.release_date
            FROM user_top_track u
            JOIN track_detail t ON u.track_id = t.id
            WHERE u.user_spotify_id = ?
            ORDER BY t.release_date ASC
            LIMIT 1
            """;
}