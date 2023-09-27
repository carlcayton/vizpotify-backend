package com.arian.vizpotifybackend.repository;

import com.arian.vizpotifybackend.model.ArtistDetail;
import com.arian.vizpotifybackend.model.RelatedArtist;
import com.arian.vizpotifybackend.model.composite.RelatedArtistId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RelatedArtistRepository extends JpaRepository<RelatedArtist, RelatedArtistId> {

    @Query("SELECT ra.relatedArtist FROM RelatedArtist ra WHERE ra.primaryArtistId = :primaryArtistId")
    List<ArtistDetail> findRelatedArtistsByPrimaryArtistId(@Param("primaryArtistId") String primaryArtistId);
}



