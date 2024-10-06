package com.arian.vizpotifybackend.track;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface TrackDetailRepository extends JpaRepository<TrackDetail, String> {

    @Query("SELECT a.id FROM TrackDetail a WHERE a.id IN :ids")
    List<String> findExistingIds(@Param("ids") Set<String> ids);

    List<TrackDetail> findByIdIn(List<String> ids);
    List<TrackDetail> findByPopularityGreaterThanEqual(int popularity);

    @Modifying
    @Query(nativeQuery = true, value = """
        INSERT INTO track_detail (id, name, artists, duration, album_name, album_image_url, popularity, release_date)
        VALUES (:#{#tracks.![id]}, :#{#tracks.![name]}, :#{#tracks.![artists]}, :#{#tracks.![duration]},
                :#{#tracks.![albumName]}, :#{#tracks.![albumImageUrl]}, :#{#tracks.![popularity]}, :#{#tracks.![releaseDate]})
        ON CONFLICT (id) DO NOTHING
        RETURNING id
    """)
    List<String> bulkInsertTrackDetails(@Param("tracks") List<TrackDetail> tracks);
}
