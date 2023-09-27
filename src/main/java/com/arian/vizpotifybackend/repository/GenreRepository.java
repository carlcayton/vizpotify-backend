package com.arian.vizpotifybackend.repository;


import com.arian.vizpotifybackend.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface GenreRepository extends JpaRepository<Genre, String> {
    @Query("SELECT g.name FROM Genre as g WHERE g.name IN :genres")
    Set<String> findExistingGenres(@Param("genres") Set<String> genres);
}
