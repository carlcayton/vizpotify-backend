
package com.arian.vizpotifybackend.artist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ArtistDetailRepository extends JpaRepository<ArtistDetail, String> {
    @Query("SELECT a.id FROM ArtistDetail a WHERE a.id IN :ids")
    List<String> findExistingIds(@Param("ids") Set<String> ids);

    List<ArtistDetail> findByIdIn(List<String> ids);
}
