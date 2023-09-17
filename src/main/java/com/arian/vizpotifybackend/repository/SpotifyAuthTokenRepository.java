package com.arian.vizpotifybackend.repository;

import com.arian.vizpotifybackend.model.SpotifyAuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpotifyAuthTokenRepository extends JpaRepository<SpotifyAuthToken, String> {



}
