package com.arian.vizpotifybackend.repository;

import com.arian.vizpotifybackend.model.RelatedArtist;
import com.arian.vizpotifybackend.model.composite.RelatedArtistId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RelatedArtistRepository extends JpaRepository<RelatedArtist, RelatedArtistId> {
    List<RelatedArtist> findByRelatedArtistId_PrimaryArtistId(String primaryArtistId);
}
