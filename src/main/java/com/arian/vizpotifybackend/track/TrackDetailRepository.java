package com.arian.vizpotifybackend.track;

import org.springframework.data.jpa.repository.JpaRepository;
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
}
