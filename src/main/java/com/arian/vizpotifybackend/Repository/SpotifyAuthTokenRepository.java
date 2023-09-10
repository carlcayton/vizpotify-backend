package com.arian.vizpotifybackend.Repository;

import com.arian.vizpotifybackend.model.SpotifyAuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpotifyAuthTokenRepository extends JpaRepository<SpotifyAuthToken, Integer> {

}
