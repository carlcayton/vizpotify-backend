package com.arian.vizpotifybackend.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpotifyAuthTokenRepository extends JpaRepository<SpotifyAuthToken, String> {



}
