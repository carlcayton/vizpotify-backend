package com.arian.vizpotifybackend.auth;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class JwtResponse {
    private final String accessToken;
    private final String spotifyId;
}
